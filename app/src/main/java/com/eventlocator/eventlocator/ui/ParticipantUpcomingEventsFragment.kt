package com.eventlocator.eventlocator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.adapters.OrganizerUpcomingEventAdapter
import com.eventlocator.eventlocator.adapters.ParticipantUpcomingEventAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.FragmentEventsBinding
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ParticipantUpcomingEventsFragment(val events: ArrayList<Event>): Fragment() {

    lateinit var binding: FragmentEventsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEventsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val status = ArrayList<String>()
        for(i in 0 until events.size){
            val registrationCloseDateTime = LocalDateTime.parse(events[i].registrationCloseDateTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))
            val startDate = LocalDate.parse(events[i].startDate,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
            val startDateTime = startDate.atTime(LocalTime.parse(events[i].sessions[0].startTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            if (LocalDateTime.now().isBefore(registrationCloseDateTime)){
                if (events[i].currentNumberOfParticipants == events[i].maxParticipants){
                    status.add(getString(R.string.event_full))
                }
                else status.add(getString(R.string.registration_ongoing))
            }
            else if (LocalDateTime.now().isBefore(startDateTime) && LocalDateTime.now().isAfter(registrationCloseDateTime)){
                status.add(getString(R.string.registration_closed))
            }
            else{
                var found = false
                for(j in 0 until events[i].sessions.size){
                    val sessionDate = LocalDate.parse(events[i].sessions[j].date,
                            DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
                    val sessionStartDateTime = sessionDate.atTime(LocalTime.parse(events[i].sessions[j].startTime,
                            DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
                    val sessionEndDateTime = sessionDate.atTime(LocalTime.parse(events[i].sessions[j].endTime,
                            DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))

                    if (LocalDateTime.now().isAfter(sessionStartDateTime) && LocalDateTime.now().isBefore(sessionEndDateTime)){
                        status.add(getString(R.string.session_happening_right_now))
                        found = true
                        break;
                    }
                }

                if (!found){
                    status.add(getString(R.string.active))
                }
            }

        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.layoutManager = layoutManager

        val adapter = ParticipantUpcomingEventAdapter(events, status)
        binding.rvEvents.adapter = adapter
        binding.rvEvents.adapter!!.notifyDataSetChanged()
    }
}