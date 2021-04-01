package com.eventlocator.eventlocator.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.adapters.UpcomingEventAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.FragmentEventsWithFilteringBinding
import com.eventlocator.eventlocator.retrofit.EventService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import com.eventlocator.eventlocator.utilities.SharedPreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class UpcomingEventsFragment: Fragment(), OnUpcomingEventsReady {

    lateinit var binding: FragmentEventsWithFilteringBinding
    lateinit var events: ArrayList<Event>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEventsWithFilteringBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as EventsActivity).onUpcomingEventsReady = this
        val token = requireContext().getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE,
        Context.MODE_PRIVATE).getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        RetrofitServiceFactory.createServiceWithAuthentication(EventService::class.java, token!!)
                .getUpcomingEvents().enqueue(object: Callback<ArrayList<Event>>{
                    override fun onResponse(call: Call<ArrayList<Event>>, response: Response<ArrayList<Event>>) {
                        //TODO: Check response codes
                        events = response.body()!!
                        val status = getStatusForEvents(events)
                        val initialEvents = ArrayList<Event>()
                        val initialStatus = ArrayList<String>()
                        //TODO: Display events based on preference and city
                        for(i in 0 until events.size){
                            if (status[i]!=getString(R.string.registration_closed) &&
                                    status[i] != "FULL" /*TODO: ADD FULL*/) {
                                initialEvents.add(events[i])
                                initialStatus.add(status[i])
                            }

                        }

                        val adapter = UpcomingEventAdapter(initialEvents, initialStatus)
                        val layoutManager = LinearLayoutManager(requireContext())
                        binding.rvEvents.layoutManager = layoutManager
                        binding.rvEvents.adapter = adapter

                    }

                    override fun onFailure(call: Call<ArrayList<Event>>, t: Throwable) {
                        //TODO: Handle failure
                    }

                })
    }


    fun getStatusForEvents(events: ArrayList<Event>): ArrayList<String> {
        val status = ArrayList<String>()
        for (i in 0 until events.size) {
            val registrationCloseDateTime = LocalDateTime.parse(events[i].registrationCloseDateTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))
            val startDate = LocalDate.parse(events[i].startDate,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
            val startDateTime = startDate.atTime(LocalTime.parse(events[i].sessions[0].startTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            if (LocalDateTime.now().isBefore(registrationCloseDateTime)) {
                status.add(getString(R.string.registration_ongoing))
            } else if (LocalDateTime.now().isBefore(startDateTime) && LocalDateTime.now().isAfter(registrationCloseDateTime)) {
                status.add(getString(R.string.registration_closed))
            } else {
                var found = false
                for (j in 0 until events[i].sessions.size) {
                    val sessionDate = LocalDate.parse(events[i].sessions[j].date,
                            DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
                    val sessionStartDateTime = sessionDate.atTime(LocalTime.parse(events[i].sessions[j].startTime,
                            DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
                    val sessionEndDateTime = sessionDate.atTime(LocalTime.parse(events[i].sessions[j].endTime,
                            DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
                    if (LocalDateTime.now().isAfter(sessionStartDateTime) && LocalDateTime.now().isBefore(sessionEndDateTime)) {
                        status.add(getString(R.string.session_happening_right_now))
                        found = true
                        break
                    }
                }
                if (!found) {
                    status.add(getString(R.string.active))
                }
            }

            //TODO: Add for full events
        }
        return status
    }

    override fun sendUpcomingEvents(upcomingEvents: ArrayList<Event>) {
        val status = getStatusForEvents(upcomingEvents)
        val adapter = UpcomingEventAdapter(upcomingEvents, status)
        binding.rvEvents.adapter = adapter
        binding.rvEvents.adapter!!.notifyDataSetChanged()
    }

}