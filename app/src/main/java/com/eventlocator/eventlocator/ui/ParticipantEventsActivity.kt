package com.eventlocator.eventlocator.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.adapters.ParticipantEventsPagerAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.ActivityEventsBinding
import com.eventlocator.eventlocator.retrofit.EventService
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
                    binding.root.closeDrawers()
                    false
                }
                R.id.dmiFollowedOrganizers -> {
                    //TODO: open followed organizers
                    binding.root.closeDrawers()
                    true
                }
                R.id.dmiSearchOrganizers -> {
                    //TODO: open search organizers
                    binding.root.closeDrawers()
                    true
                }
                R.id.dmiEditProfile -> {
                    //TODO: open get profile
                    binding.root.closeDrawers()
                    true
                }
                R.id.dmiLogout -> {
                    val sharedPreferenceEditor =
                            getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE).edit()
                    sharedPreferenceEditor.putString(SharedPreferenceManager.instance.TOKEN_KEY, null)
                    sharedPreferenceEditor.apply()
                    //TODO: add confirmation box for logout
                    binding.root.closeDrawers()
                    startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                else -> {
                    false
                }
            }
        }

        getAndLoadEvents()

    }

    private fun getAndLoadEvents(){
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

                    }

                    override fun onFailure(call: Call<ArrayList<Event>>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@ParticipantEventsActivity,
                                "Error", "Can't connect to server",false)
                    }

                })
    }

}
