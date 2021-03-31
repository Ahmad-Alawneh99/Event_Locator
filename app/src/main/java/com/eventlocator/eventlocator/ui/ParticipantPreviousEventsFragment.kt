package com.eventlocator.eventlocator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eventlocator.eventlocator.adapters.OrganizerCanceledEventAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.FragmentEventsBinding


class ParticipantPreviousEventsFragment(val events: ArrayList<Event>): Fragment() {

    lateinit var binding: FragmentEventsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEventsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.layoutManager = layoutManager
        binding.rvEvents.addItemDecoration(DividerItemDecoration(requireContext(),layoutManager.orientation))
        val adapter = OrganizerCanceledEventAdapter(events)
        binding.rvEvents.adapter = adapter
    }
}