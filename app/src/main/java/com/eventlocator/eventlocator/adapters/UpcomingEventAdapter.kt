package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.UpcomingEventBinding
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate

class UpcomingEventAdapter(var events: ArrayList<Event>, var status: ArrayList<String>):
        RecyclerView.Adapter<UpcomingEventAdapter.UpcomingEventHolder>() {
    lateinit var context: Context
    inner class UpcomingEventHolder(val binding: UpcomingEventBinding): RecyclerView.ViewHolder(binding.root){
        //TODO: add on click on the root to open the activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingEventHolder {
        val binding = UpcomingEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return UpcomingEventHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingEventHolder, position: Int) {
        holder.binding.tvEventID.text = events[position].id.toString()
        holder.binding.tvEventName.text = events[position].name
        holder.binding.tvOrganizerName.text = events[position].organizerName
        val startDate = LocalDate.parse(events[position].startDate,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        val endDate = LocalDate.parse(events[position].endDate,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        holder.binding.tvEventDates.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                .format(startDate) + " - " +
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY).format(endDate)
        holder.binding.tvEventStatus.text = status[position]
    }

    override fun getItemCount(): Int {
        return events.size
    }

}