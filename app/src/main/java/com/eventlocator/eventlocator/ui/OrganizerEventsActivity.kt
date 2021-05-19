package com.eventlocator.eventlocator.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.adapters.OrganizerEventsPagerAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.ActivityEventsBinding
import com.eventlocator.eventlocator.retrofit.EventService
import com.eventlocator.eventlocator.retrofit.OrganizerService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.SharedPreferenceManager
import com.eventlocator.eventlocator.utilities.Utils
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrganizerEventsActivity : AppCompatActivity(), OnPreviousEventsFiltered {
    lateinit var binding: ActivityEventsBinding
    var filterFragment: FilterOrganizersPreviousEventsFragment? = null
    lateinit var onPreviousEventsReady: OnPreviousEventsReady
    lateinit var pagerAdapter: OrganizerEventsPagerAdapter
    var currentPosition = 0
    var organizerID: Long = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mtbToolbar.visibility = View.GONE
        binding.dlParticipant.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        organizerID = intent.getLongExtra("organizerID", -1)
        getAndLoadEvents()
    }

    private fun getAndLoadEvents(){
        binding.pbLoading.visibility = View.VISIBLE
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        RetrofitServiceFactory.createServiceWithAuthentication(OrganizerService::class.java, token!!)
                .getOrganizerEvents(organizerID).enqueue(object: Callback<ArrayList<Event>> {
                    override fun onResponse(call: Call<ArrayList<Event>>, response: Response<ArrayList<Event>>) {
                        if (response.code() == 202) {
                            invalidateOptionsMenu()
                            pagerAdapter = OrganizerEventsPagerAdapter(this@OrganizerEventsActivity, 3, response.body()!!)
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
                                    currentPosition = position
                                    invalidateOptionsMenu()
                                    if (position != 1 && filterFragment != null) {
                                        supportFragmentManager.commit {
                                            remove(filterFragment!!)
                                            filterFragment = null
                                        }
                                    }
                                }

                            })
                        }
                        else if (response.code()==401){
                            Utils.instance.displayInformationalDialog(this@OrganizerEventsActivity, "Error",
                                    "401: Unauthorized access",true)
                        }
                        else if (response.code() == 404) {
                            Utils.instance.displayInformationalDialog(this@OrganizerEventsActivity,
                                    "Error", "No events found", false)
                        }
                        else if (response.code() == 500) {
                            Utils.instance.displayInformationalDialog(this@OrganizerEventsActivity,
                                    "Error", "Server issue, please try again later", false)
                        }
                        binding.pbLoading.visibility = View.INVISIBLE
                    }

                    override fun onFailure(call: Call<ArrayList<Event>>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@OrganizerEventsActivity,
                                "Error", "Can't connect to server", false)
                        binding.pbLoading.visibility = View.INVISIBLE
                    }

                })

    }

    override fun getPreviousEvents(previousEvents: ArrayList<Event>) {
        onPreviousEventsReady.sendPreviousEvents(previousEvents)
        supportFragmentManager.commit {
            remove(filterFragment!!)
            filterFragment = null
        }
    }

     override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
         if (!this::pagerAdapter.isInitialized || currentPosition!=1)return false
         menu?.add(1, 1, Menu.NONE, "Filter").also { item ->
             item?.icon = ContextCompat.getDrawable(this, R.drawable.ic_filter)
             item?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
         }
         return super.onPrepareOptionsMenu(menu)
     }

     override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
             1 -> {
                 if (currentPosition == 1) {
                     if (filterFragment == null) {
                         filterFragment = FilterOrganizersPreviousEventsFragment(pagerAdapter.previousEvents)
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
         }
         return super.onOptionsItemSelected(item)
     }
}

interface OnPreviousEventsReady{
    fun sendPreviousEvents(previousEvents: ArrayList<Event>)
}