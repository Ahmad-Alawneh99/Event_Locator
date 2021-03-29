package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.UpcomingEventOrganizersProfileBinding
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate

class OrganizerUpcomingEventAdapter(private val events: ArrayList<Event>, private val status: ArrayList<String>):
        RecyclerView.Adapter<OrganizerUpcomingEventAdapter.OrganizerUpcomingEventHolder>() {
    lateinit var context: Context
    inner class OrganizerUpcomingEventHolder(var binding: UpcomingEventOrganizersProfileBinding):
            RecyclerView.ViewHolder(binding.root){

        init{
            //TODO: Set click listener
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizerUpcomingEventHolder {
        val binding = UpcomingEventOrganizersProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return OrganizerUpcomingEventHolder(binding)
    }

    override fun onBindViewHolder(holder: OrganizerUpcomingEventHolder, position: Int) {
        holder.binding.tvEventID.text = events[position].id.toString()
        holder.binding.tvEventName.text = events[position].id.toString()
        val startDate = LocalDate.parse(events[position].startDate,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        val endDate = LocalDate.parse(events[position].endDate,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        holder.binding.tvEventDates.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                .format(startDate) + " - " +
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY).format(endDate)

        holder.binding.tvEventStatus.text = status[position]
    }

    override fun getItemCount(): Int = events.size
}