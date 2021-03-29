package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.UpcomingEventParticipantsProfileBinding
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate

class ParticipantUpcomingEventAdapter(private val events: ArrayList<Event>, private val status: ArrayList<String>):
        RecyclerView.Adapter<ParticipantUpcomingEventAdapter.ParticipantUpcomingEventViewHolder>(){

    lateinit var context: Context

    inner class ParticipantUpcomingEventViewHolder(var binding: UpcomingEventParticipantsProfileBinding):
            RecyclerView.ViewHolder(binding.root){

        init{
            //TODO: Set click listener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantUpcomingEventViewHolder {
        val binding = UpcomingEventParticipantsProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context =parent.context
        return ParticipantUpcomingEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantUpcomingEventViewHolder, position: Int) {
        holder.binding.tvEventID.text = events[position].id.toString()
        holder.binding.tvEventName.text = events[position].id.toString()
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

    override fun getItemCount(): Int = events.size

}