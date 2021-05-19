package com.eventlocator.eventlocator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eventlocator.eventlocator.adapters.OrganizerPreviousEventAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.FragmentEventsWithFilteringBinding

class OrganizerPreviousEventsFragment(var events: ArrayList<Event>): Fragment(), OnPreviousEventsReady {

    lateinit var binding: FragmentEventsWithFilteringBinding
    constructor(): this(ArrayList<Event>())
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEventsWithFilteringBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.layoutManager = layoutManager
        val adapter = OrganizerPreviousEventAdapter(events)
        binding.rvEvents.adapter = adapter
        (activity as OrganizerEventsActivity).onPreviousEventsReady = this
    }

    override fun sendPreviousEvents(previousEvents: ArrayList<Event>) {
        this.events = previousEvents
        val adapter = OrganizerPreviousEventAdapter(events)
        binding.rvEvents.adapter = adapter
        binding.rvEvents.adapter!!.notifyDataSetChanged()
    }
}