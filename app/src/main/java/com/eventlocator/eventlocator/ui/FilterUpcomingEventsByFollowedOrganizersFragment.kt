package com.eventlocator.eventlocator.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.FragmentFilterUpcomingEventsByFollowedOrganizersBinding

class FilterUpcomingEventsByFollowedOrganizersFragment(var eventList: ArrayList<Event>): Fragment() {

    lateinit var binding:FragmentFilterUpcomingEventsByFollowedOrganizersBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFilterUpcomingEventsByFollowedOrganizersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}