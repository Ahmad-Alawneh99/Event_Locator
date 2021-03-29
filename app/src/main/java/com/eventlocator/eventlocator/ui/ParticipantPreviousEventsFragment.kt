package com.eventlocator.eventlocator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.FragmentEventsWithFilteringBinding

class ParticipantPreviousEventsFragment(val events: ArrayList<Event>): Fragment() {

    lateinit var binding: FragmentEventsWithFilteringBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEventsWithFilteringBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}