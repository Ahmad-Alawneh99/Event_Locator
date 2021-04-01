package com.eventlocator.eventlocator.ui

import android.os.Bundle

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.adapters.ParticipantEventsPagerAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.ActivityEventsBinding
import com.eventlocator.eventlocator.retrofit.EventService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.SharedPreferenceManager
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

        getAndLoadEvents()

    }

    private fun getAndLoadEvents(){
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")

        RetrofitServiceFactory.createServiceWithAuthentication(EventService::class.java, token!!)
                .getParticipantEvents().enqueue(object: Callback<ArrayList<Event>> {
                    override fun onResponse(call: Call<ArrayList<Event>>, response: Response<ArrayList<Event>>) {
                        //TODO: Check response codes
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

                        binding.pagerEvents.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) {
                                super.onPageSelected(position)
                                //TODO: Hide fragment if displayed
                                Toast.makeText(applicationContext, "text", Toast.LENGTH_SHORT).show()
                            }

                        })

                    }

                    override fun onFailure(call: Call<ArrayList<Event>>, t: Throwable) {
                        //TODO: Handle failure
                    }

                })
    }

}
