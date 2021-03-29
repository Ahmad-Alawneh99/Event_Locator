package com.eventlocator.eventlocator.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.ui.ParticipantCanceledEventsFragment
import com.eventlocator.eventlocator.ui.ParticipantPreviousEventsFragment
import com.eventlocator.eventlocator.ui.ParticipantUpcomingEventsFragment
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ParticipantEventsPagerAdapter(fa: FragmentActivity, var numberOfTabs: Int, val events: ArrayList<Event>): FragmentStateAdapter(fa) {

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
            0 -> return ParticipantUpcomingEventsFragment(upcomingEvents)
            1 -> return ParticipantPreviousEventsFragment(previousEvents)
            2 -> return ParticipantCanceledEventsFragment(canceledEvents)
        }
        return ParticipantUpcomingEventsFragment(upcomingEvents)
    }

    private fun filterEvents(){
        for(i in 0 until events.size){
            val eventDate = LocalDate.parse(events[i].endDate, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
            val eventDateTime = eventDate.atTime(LocalTime.parse(events[i].sessions[events[i].sessions.size-1].endTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            if (events[i].canceledEventData!=null){
                canceledEvents.add(events[i])
            }
            else if (LocalDateTime.now().isAfter(eventDateTime)){
                previousEvents.add(events[i])
            }
            else{
                upcomingEvents.add(events[i])
            }
        }
    }
}