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
                            val initialEvents = ArrayList<Event>()
                            for (i in 0 until events.size) {
                                if (!events[i].isRegistrationClosed() &&
                                        !events[i].isFull() && events[i].canceledEventData==null) {
                                    initialEvents.add(events[i])
                                }
                            }
                            val adapter = UpcomingEventAdapter(initialEvents)
                            val layoutManager = LinearLayoutManager(requireContext())
                            binding.rvEvents.layoutManager = layoutManager
                            binding.rvEvents.adapter = adapter
                            activity?.invalidateOptionsMenu()
                        }
                        else if (response.code()==401){
                            Utils.instance.displayInformationalDialog(this@UpcomingEventsByFollowedOrganizersFragment.requireContext(),
                                    "Error", "401: Unauthorized access",true)
                        }
                        else if (response.code()==404){
                            events = ArrayList()
                        }
                        else if (response.code()==500){
                            Utils.instance.displayInformationalDialog(this@UpcomingEventsByFollowedOrganizersFragment.requireContext(),
                                    "Error", "Server issue, please try again later",false)
                        }
                        (activity as EventsActivity).binding.pbLoading.visibility = View.INVISIBLE

                    }
                    override fun onFailure(call: Call<ArrayList<Event>>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@UpcomingEventsByFollowedOrganizersFragment.requireContext(),
                                "Error", "Can't connect to the server",false)
                        (activity as EventsActivity).binding.pbLoading.visibility = View.INVISIBLE
                    }

                })
    }

}