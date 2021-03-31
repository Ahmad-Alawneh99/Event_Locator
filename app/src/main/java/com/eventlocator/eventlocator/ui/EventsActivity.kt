package com.eventlocator.eventlocator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.adapters.UpcomingEventsPagerAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.ActivityEventsBinding
import com.google.android.material.tabs.TabLayoutMediator

class EventsActivity : AppCompatActivity(), OnUpcomingEventsFiltered, OnUpcomingEventsByFollowedOrganizersFiltered {
    lateinit var binding: ActivityEventsBinding
    lateinit var onUpcomingEventsReady: OnUpcomingEventsReady
    lateinit var onUpcomingEventsByFollowedOrganizersReady: OnUpcomingEventsByFollowedOrganizersReady
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mtbToolbar.setNavigationOnClickListener {
            binding.dlParticipant.openDrawer(GravityCompat.START)
        }

        //TODO: Do the thing for rating events

        val pagerAdapter = UpcomingEventsPagerAdapter(this, 2)
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
                Toast.makeText(applicationContext, "text", Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun getUpcomingEvents(upcomingEvents: ArrayList<Event>) {
        onUpcomingEventsReady.sendUpcomingEvents(upcomingEvents)
    }

    override fun getUpcomingEventsByFollowedOrganizers(upcomingEvents: ArrayList<Event>) {
        onUpcomingEventsByFollowedOrganizersReady.sendUpcomingEventsByFollowedOrganizers(upcomingEvents)
    }
}

interface OnUpcomingEventsReady{
    fun sendUpcomingEvents(upcomingEvents: ArrayList<Event>)
}

interface OnUpcomingEventsByFollowedOrganizersReady{
    fun sendUpcomingEventsByFollowedOrganizers(upcomingEvents: ArrayList<Event>)
}