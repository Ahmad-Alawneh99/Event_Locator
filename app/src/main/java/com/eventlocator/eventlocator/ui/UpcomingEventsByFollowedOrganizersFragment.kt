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
import com.eventlocator.eventlocator.utilities.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class UpcomingEventsByFollowedOrganizersFragment: Fragment(), OnUpcomingEventsByFollowedOrganizersReady {
    lateinit var binding: FragmentEventsWithFilteringBinding
    lateinit var events: ArrayList<Event>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEventsWithFilteringBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as EventsActivity).onUpcomingEventsByFollowedOrganizersReady = this
        getAndLoadEvents()
    }

    /*private fun getStatusForEvents(events: ArrayList<Event>): ArrayList<String> {
        val status = ArrayList<String>()
        for (i in 0 until events.size) {
            val registrationCloseDateTime = LocalDateTime.parse(events[i].registrationCloseDateTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))
            val startDate = LocalDate.parse(events[i].startDate,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
            val startDateTime = startDate.atTime(LocalTime.parse(events[i].sessions[0].startTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            if (LocalDateTime.now().isBefore(registrationCloseDateTime)) {
                if (events[i].currentNumberOfParticipants == events[i].maxParticipants){
                    status.add(getString(R.string.event_full))
                }
                else status.add(getString(R.string.registration_ongoing))
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

        }
        return status
    }*/

    override fun sendUpcomingEventsByFollowedOrganizers(upcomingEvents: ArrayList<Event>) {
        val adapter = UpcomingEventAdapter(upcomingEvents)
        binding.rvEvents.adapter = adapter
        binding.rvEvents.adapter!!.notifyDataSetChanged()
    }

    fun getAndLoadEvents(){
        val token = requireContext().getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE,
                Context.MODE_PRIVATE).getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        RetrofitServiceFactory.createServiceWithAuthentication(EventService::class.java,token!!)
                .getUpcomingEventsByFollowedOrganizers().enqueue(object: Callback<ArrayList<Event>>{
                    override fun onResponse(call: Call<ArrayList<Event>>, response: Response<ArrayList<Event>>) {
                        if (response.code()==202) {
                            events = response.body()!!
                            //val status = getStatusForEvents(events)
                            val initialEvents = ArrayList<Event>()
                            //val initialStatus = ArrayList<String>()
                            for (i in 0 until events.size) {
                                if (events[i].isRegistrationClosed() &&
                                        events[i].isFull() && events[i].canceledEventData==null) {
                                    initialEvents.add(events[i])
                                }
                            }
                            val adapter = UpcomingEventAdapter(initialEvents)
                            val layoutManager = LinearLayoutManager(requireContext())
                            binding.rvEvents.layoutManager = layoutManager
                            binding.rvEvents.adapter = adapter
                        }
                        else if (response.code()==401){
                            Utils.instance.displayInformationalDialog(this@UpcomingEventsByFollowedOrganizersFragment.requireContext(),
                                    "Error", "401: Unauthorized access",true)
                        }
                        else if (response.code()==404){
                            //TODO: Find a better way to do this
                            Utils.instance.displayInformationalDialog(this@UpcomingEventsByFollowedOrganizersFragment.requireContext(),
                                    "Error", "No events found",false)
                        }
                        else if (response.code()==500){
                            Utils.instance.displayInformationalDialog(this@UpcomingEventsByFollowedOrganizersFragment.requireContext(),
                                    "Error", "Server issue, please try again later",false)
                        }

                    }
                    override fun onFailure(call: Call<ArrayList<Event>>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@UpcomingEventsByFollowedOrganizersFragment.requireContext(),
                                "Error", "Can't connect to the server",false)
                    }

                })
    }

}