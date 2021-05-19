package com.eventlocator.eventlocator.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.adapters.UpcomingEventsPagerAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.data.Participant
import com.eventlocator.eventlocator.databinding.ActivityEventsBinding
import com.eventlocator.eventlocator.retrofit.ParticipantService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.SharedPreferenceManager
import com.eventlocator.eventlocator.utilities.Utils
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsActivity : AppCompatActivity(), OnUpcomingEventsFiltered, OnUpcomingEventsByFollowedOrganizersFiltered {
    lateinit var binding: ActivityEventsBinding
    lateinit var onUpcomingEventsReady: OnUpcomingEventsReady
    lateinit var onUpcomingEventsByFollowedOrganizersReady: OnUpcomingEventsByFollowedOrganizersReady
    lateinit var participant: Participant
    lateinit var pagerAdapter: UpcomingEventsPagerAdapter
    lateinit var editProfileActivityResult: ActivityResultLauncher<Intent>
    var filterFragment: Fragment? = null
    var currentPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mtbToolbar)
        title = "Events"
        val sharedPreferences = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
        if (!sharedPreferences.contains(SharedPreferenceManager.instance.FIRST_TIME_KEY)){
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
        if (sharedPreferences.getString(SharedPreferenceManager.instance.TOKEN_KEY,"") ==""){
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
        else {
            editProfileActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
                getParticipantAndLoadEvents()
            }
            getParticipantAndLoadEvents()
        }
    }

    override fun getUpcomingEvents(upcomingEvents: ArrayList<Event>) {
        onUpcomingEventsReady.sendUpcomingEvents(upcomingEvents)
        supportFragmentManager.commit {
            remove(filterFragment!!)
            filterFragment = null
        }
    }

    override fun getUpcomingEventsByFollowedOrganizers(upcomingEvents: ArrayList<Event>) {
        onUpcomingEventsByFollowedOrganizersReady.sendUpcomingEventsByFollowedOrganizers(upcomingEvents)
        supportFragmentManager.commit {
            remove(filterFragment!!)
            filterFragment = null
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }

    override fun onResume() {
        super.onResume()
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        if (token == "EMPTY"){
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (binding.pbLoading.visibility == View.INVISIBLE) {
            menu!!.add(1, 1, 2, "Filter").also { item ->
                item?.icon = ContextCompat.getDrawable(this, R.drawable.ic_filter)
                item?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }
        }
        if (this::pagerAdapter.isInitialized){
            menu!!.add(1, 2, 1, "Refresh").also { item ->
                item?.icon = ContextCompat.getDrawable(this, R.drawable.ic_refresh)
                item?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            1 -> {
                if (filterFragment!=null){
                    supportFragmentManager.commit {
                        remove(filterFragment!!)
                        filterFragment = null
                    }
                }
                else{
                    if (currentPosition == 0){
                        filterFragment = FilterUpcomingEventsFragment(pagerAdapter.upcomingEventsFragment.events)
                        supportFragmentManager.commit {
                            add(R.id.fvFilter,filterFragment!!)
                        }
                    }
                    else{
                        filterFragment = FilterUpcomingEventsByFollowedOrganizersFragment(pagerAdapter.upcomingEventsFragment.events)
                        supportFragmentManager.commit {
                            add(R.id.fvFilter,filterFragment!!)
                        }
                    }
                }
                return true
            }
            2 -> {
                binding.pbLoading.visibility = View.VISIBLE
                invalidateOptionsMenu()
                pagerAdapter.requestEventsUpdate()
                return true
            }
            else -> return false
        }
    }

    private fun getParticipantAndLoadEvents(){
        binding.pbLoading.visibility = View.VISIBLE
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        RetrofitServiceFactory.createServiceWithAuthentication(ParticipantService::class.java,token!!)
                .getParticipantInfo().enqueue(object : Callback<Participant> {
                    override fun onResponse(call: Call<Participant>, response: Response<Participant>) {
                        if (response.code() == 202) {
                            participant = response.body()!!
                            getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                                .edit().putLong(SharedPreferenceManager.instance.PARTICIPANT_ID_KEY, participant.id).apply()
                            loadEvents()
                        }
                        else if (response.code() == 401) {
                            Utils.instance.displayInformationalDialog(this@EventsActivity, "Error",
                                    "401: Unauthorized access", true)
                        }
                        else if (response.code() == 403) {
                            Utils.instance.displayInformationalDialog(this@EventsActivity, "Error",
                                    "Your account has been suspended", true)
                            getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE).edit()
                                    .putString(SharedPreferenceManager.instance.TOKEN_KEY, null).apply()
                        }
                        else if (response.code() == 404) {
                            Utils.instance.displayInformationalDialog(this@EventsActivity, "Error",
                                    "404: Information not found", true)
                        }
                        else if (response.code() == 500) {
                            Utils.instance.displayInformationalDialog(this@EventsActivity, "Error",
                                    "Server issue, please try again later", true)
                        }
                        binding.pbLoading.visibility = View.INVISIBLE
                    }

                    override fun onFailure(call: Call<Participant>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@EventsActivity, "Error",
                                "Can't connect to the server", true)
                        binding.pbLoading.visibility = View.INVISIBLE
                    }

                })
    }

    fun loadEvents(){
        binding.mtbToolbar.setNavigationOnClickListener {
            binding.dlParticipant.openDrawer(GravityCompat.START)
        }
        binding.nvParticipant.getHeaderView(0).findViewById<TextView>(R.id.tvParticipantName).text =
                participant.firstName + " " + participant.lastName
        binding.nvParticipant.getHeaderView(0).findViewById<TextView>(R.id.tvParticipantEmail).text = participant.email
        binding.nvParticipant.getHeaderView(0).findViewById<TextView>(R.id.tvParticipantRating).text =
                participant.rating.toString() + "/5"
        pagerAdapter = UpcomingEventsPagerAdapter(this, 2)
        binding.pagerEvents.adapter = pagerAdapter

        TabLayoutMediator(binding.tlEvents, binding.pagerEvents){ tab, position ->
            when (position){
                0 -> tab.text = getString(R.string.upcoming_events)
                1 -> tab.text = getString(R.string.upcoming_events_by_followed_organizers)
            }

        }.attach()

        binding.pagerEvents.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                if (filterFragment!=null){
                    supportFragmentManager.commit {
                        remove(filterFragment!!)
                        filterFragment = null
                    }
                }
            }

        })

        binding.nvParticipant.setNavigationItemSelectedListener { item: MenuItem ->
            when(item.itemId){
                R.id.dmiMyEvents -> {
                    startActivity(Intent(this, ParticipantEventsActivity::class.java))
                    binding.dlParticipant.closeDrawers()
                    true
                }
                R.id.dmiFollowedOrganizers -> {
                    startActivity(Intent(this, FollowedOrganizersActivity::class.java))
                    binding.dlParticipant.closeDrawers()
                    true
                }
                R.id.dmiSearchOrganizers -> {
                    startActivity(Intent(this, SearchOrganizersActivity::class.java))
                    binding.dlParticipant.closeDrawers()
                    true
                }
                R.id.dmiEditProfile -> {
                    val intent = Intent(this, EditProfileActivity::class.java)
                    intent.putExtra("participant", participant)
                    editProfileActivityResult.launch(intent)
                    binding.dlParticipant.closeDrawers()
                    true
                }
                R.id.dmiLogout -> {
                    val sharedPreferenceEditor =
                            getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE).edit()
                    sharedPreferenceEditor.putString(SharedPreferenceManager.instance.TOKEN_KEY, null)
                    sharedPreferenceEditor.putLong(SharedPreferenceManager.instance.PARTICIPANT_ID_KEY, -1)
                    sharedPreferenceEditor.apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    binding.dlParticipant.closeDrawers()
                    finish()
                    true
                }
                else -> {
                    false
                }

            }

        }
    }

    fun isParticipantInitialized() = this::participant.isInitialized

}

interface OnUpcomingEventsReady{
    fun sendUpcomingEvents(upcomingEvents: ArrayList<Event>)
}

interface OnUpcomingEventsByFollowedOrganizersReady{
    fun sendUpcomingEventsByFollowedOrganizers(upcomingEvents: ArrayList<Event>)
}