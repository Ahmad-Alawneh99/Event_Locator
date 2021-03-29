package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.CanceledEventOrganizersProfileBinding
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate
import java.time.LocalDateTime

class OrganizerCanceledEventAdapter (private val events: ArrayList<Event>):
        RecyclerView.Adapter<OrganizerCanceledEventAdapter.OrganizerCanceledEventHolder>() {

    lateinit var context: Context
    inner class OrganizerCanceledEventHolder(var binding: CanceledEventOrganizersProfileBinding):
            RecyclerView.ViewHolder(binding.root){

        init{
            //TODO: Set click listener
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizerCanceledEventHolder {
        val binding = CanceledEventOrganizersProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return OrganizerCanceledEventHolder(binding)
    }

    override fun onBindViewHolder(holder: OrganizerCanceledEventHolder, position: Int) {
        holder.binding.tvEventID.text = events[position].id.toString()
        holder.binding.tvEventName.text = events[position].name
        val startDate = LocalDate.parse(events[position].startDate,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        val endDate = LocalDate.parse(events[position].endDate,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        holder.binding.tvEventDates.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                .format(startDate) + " - " +
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY).format(endDate)

        val cancellationDateTime: LocalDateTime = LocalDateTime.parse(events[position].canceledEventData!!.cancellationDateTime,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))
        holder.binding.tvCancellationDateTime.text =
                "Canceled on: " + DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DISPLAY)
                        .format(cancellationDateTime)
    }

    override fun getItemCount(): Int = events.size
}