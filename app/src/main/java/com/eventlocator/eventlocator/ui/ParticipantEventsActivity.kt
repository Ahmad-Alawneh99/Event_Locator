package com.eventlocator.eventlocator.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.adapters.ParticipantEventsPagerAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.data.Participant
import com.eventlocator.eventlocator.databinding.ActivityEventsBinding
import com.eventlocator.eventlocator.retrofit.EventService
import com.eventlocator.eventlocator.retrofit.ParticipantService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.SharedPreferenceManager
import com.eventlocator.eventlocator.utilities.Utils
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ParticipantEventsActivity : AppCompatActivity() {
    lateinit var binding: ActivityEventsBinding
    lateinit var participant: Participant
    lateinit var editProfileActivityResult: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mtbToolbar.setNavigationOnClickListener {
            binding.dlParticipant.openDrawer(GravityCompat.START)
        }

        binding.nvParticipant.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.dmiMyEvents -> {
                    binding.dlParticipant.closeDrawers()
                    false
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
                    sharedPreferenceEditor.apply()
                    binding.dlParticipant.closeDrawers()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> {
                    false
                }
            }
        }
        editProfileActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            getParticipantAndLoadEvents()
        }
        getParticipantAndLoadEvents()

    }

    private fun getParticipantAndLoadEvents(){
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        RetrofitServiceFactory.createServiceWithAuthentication(ParticipantService::class.java,token!!)
                .getParticipantInfo().enqueue(object : Callback<Participant> {
                    override fun onResponse(call: Call<Participant>, response: Response<Participant>) {
                        if (response.code() == 202) {
                            participant = response.body()!!
                            getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                                    .edit().putLong(SharedPreferenceManager.instance.PARTICIPANT_ID_KEY, participant.id).apply()

                            binding.nvParticipant.getHeaderView(0).findViewById<TextView>(R.id.tvParticipantName).text =
                                    participant.firstName + " " + participant.lastName
                            binding.nvParticipant.getHeaderView(0).findViewById<TextView>(R.id.tvParticipantEmail).text = participant.email
                            binding.nvParticipant.getHeaderView(0).findViewById<TextView>(R.id.tvParticipantRating).text =
                                    participant.rating.toString() + "/5"
                            getAndLoadEvents()
                        }
                        else if (response.code() == 401) {
                            Utils.instance.displayInformationalDialog(this@ParticipantEventsActivity, "Error",
                                    "401: Unauthorized access", true)
                            binding.pbLoading.visibility = View.INVISIBLE
                        }
                        else if (response.code() == 403) {
                            Utils.instance.displayInformationalDialog(this@ParticipantEventsActivity, "Error",
                                    "Your account has been suspended", true)
                            getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE).edit()
                                    .putString(SharedPreferenceManager.instance.TOKEN_KEY, null).apply()
                        }
                        else if (response.code() == 404) {
                            Utils.instance.displayInformationalDialog(this@ParticipantEventsActivity, "Error",
                                    "404: Information not found", true)
                            binding.pbLoading.visibility = View.INVISIBLE
                        }
                        else if (response.code() == 500) {
                            Utils.instance.displayInformationalDialog(this@ParticipantEventsActivity, "Error",
                                    "Server issue, please try again later", false)
                            binding.pbLoading.visibility = View.INVISIBLE
                        }
                    }

                    override fun onFailure(call: Call<Participant>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@ParticipantEventsActivity, "Error",
                                "Can't connect to the server", true)
                        binding.pbLoading.visibility = View.INVISIBLE
                    }

                })
    }

    private fun getAndLoadEvents(){
        binding.pbLoading.visibility = View.VISIBLE
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")

        RetrofitServiceFactory.createServiceWithAuthentication(EventService::class.java, token!!)
                .getParticipantEvents().enqueue(object: Callback<ArrayList<Event>> {
                    override fun onResponse(call: Call<ArrayList<Event>>, response: Response<ArrayList<Event>>) {
                        if (response.code()==202) {
                            val pagerAdapter = ParticipantEventsPagerAdapter(this@ParticipantEventsActivity, 3,
                                    response.body()!!)
                            binding.pagerEvents.adapter = pagerAdapter
                            TabLayoutMediator(binding.tlEvents, binding.pagerEvents) { tab, position ->
                                when (position) {
                                    0 -> tab.text = getString(R.string.upcoming_events)
                                    1 -> tab.text = getString(R.string.previous_events)
                                    2 -> tab.text = getString(R.string.canceled_events)
                                }

                            }.attach()
                        }
                        else if (response.code()==401){
                            Utils.instance.displayInformationalDialog(this@ParticipantEventsActivity, "Error",
                                    "401: Unauthorized access",true)
                        }
                        else if (response.code() == 404){
                            Utils.instance.displayInformationalDialog(this@ParticipantEventsActivity,
                                    "Error", "No events found",false)
                        }
                        else if (response.code() == 500){
                            Utils.instance.displayInformationalDialog(this@ParticipantEventsActivity,
                                    "Error", "Server issue, please try again later",false)
                        }

                        binding.pbLoading.visibility = View.INVISIBLE

                    }

                    override fun onFailure(call: Call<ArrayList<Event>>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@ParticipantEventsActivity,
                                "Error", "Can't connect to server",false)
                        binding.pbLoading.visibility = View.INVISIBLE
                    }

                })
    }

}
