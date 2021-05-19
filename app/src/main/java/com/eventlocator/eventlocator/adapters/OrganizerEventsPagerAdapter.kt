package com.eventlocator.eventlocator.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.ui.*
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class OrganizerEventsPagerAdapter(fa: FragmentActivity, var numberOfTabs: Int, val events: ArrayList<Event>): FragmentStateAdapter(fa) {
    var upcomingEvents = ArrayList<Event>()
    var previousEvents = ArrayList<Event>()
    var canceledEvents = ArrayList<Event>()
    init{
        filterEvents()
    }
    override fun getItemCount(): Int {
        return numberOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return OrganizerUpcomingEventsFragment(upcomingEvents)
            1 -> return OrganizerPreviousEventsFragment(previousEvents)
            2 -> return OrganizerCanceledEventsFragment(canceledEvents)
        }
        return OrganizerUpcomingEventsFragment(upcomingEvents)
    }

    private fun filterEvents(){
        for(i in 0 until events.size){
            if (events[i].isCanceled()){
                canceledEvents.add(events[i])
            }
            else if (events[i].isFinished()){
                previousEvents.add(events[i])
            }
            else{
                upcomingEvents.add(events[i])
            }
        }
    }
}