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
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.layoutManager = layoutManager
        val adapter = ParticipantUpcomingEventAdapter(events)
        binding.rvEvents.adapter = adapter
        binding.rvEvents.adapter!!.notifyDataSetChanged()
    }
}