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
    //var filterFragment: FilterPreviousEventsFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

   /* override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menu?.add(1, 1, Menu.NONE, "Filter").also { item ->
            item?.icon = ContextCompat.getDrawable(this, R.drawable.ic_temp)
            item?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                if (filterFragment == null) {
                    filterFragment = FilterPreviousEventsFragment(ArrayList())
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        add(R.id.fvFilter, filterFragment!!)
                    }
                } else {
                    supportFragmentManager.commit {
                        remove(filterFragment!!)
                        filterFragment = null
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }*/
}