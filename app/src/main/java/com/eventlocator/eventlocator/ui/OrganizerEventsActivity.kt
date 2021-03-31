package com.eventlocator.eventlocator.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.adapters.OrganizerEventsPagerAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.ActivityEventsBinding
import com.google.android.material.tabs.TabLayoutMediator

class OrganizerEventsActivity : AppCompatActivity(), OnPreviousEventsFiltered {
    lateinit var binding: ActivityEventsBinding
    var filterFragment: FilterOrganizersPreviousEventsFragment? = null
    lateinit var onPreviousEventsReady: OnPreviousEventsReady
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO: Get data from backend
        val pagerAdapter = OrganizerEventsPagerAdapter(this, 3, ArrayList())
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
                Toast.makeText(applicationContext, "text", Toast.LENGTH_SHORT).show()
            }

        })


    }

    override fun getPreviousEvents(previousEvents: ArrayList<Event>) {
        onPreviousEventsReady.sendPreviousEvents(previousEvents)
    }

     override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
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
                     filterFragment = FilterOrganizersPreviousEventsFragment(ArrayList())//TODO: add data from adapter
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
     }
}

interface OnPreviousEventsReady{
    fun sendPreviousEvents(previousEvents: ArrayList<Event>)
}