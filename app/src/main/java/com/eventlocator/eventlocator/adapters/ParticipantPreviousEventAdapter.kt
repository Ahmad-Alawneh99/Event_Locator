package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.PreviousEventParticipantsProfileBinding
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate

class ParticipantPreviousEventAdapter(private val events: ArrayList<Event>):
        RecyclerView.Adapter<ParticipantPreviousEventAdapter.ParticipantPreviousEventHolder>() {

    lateinit var context: Context

    inner class ParticipantPreviousEventHolder(var binding: PreviousEventParticipantsProfileBinding):
            RecyclerView.ViewHolder(binding.root){
        init {
            //TODO: Add click listener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantPreviousEventHolder {
        val binding = PreviousEventParticipantsProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ParticipantPreviousEventHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantPreviousEventHolder, position: Int) {
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

        holder.binding.tvEventRating.text = events[position].rating.toString() + "/5"
    }

    override fun getItemCount(): Int = events.size
}