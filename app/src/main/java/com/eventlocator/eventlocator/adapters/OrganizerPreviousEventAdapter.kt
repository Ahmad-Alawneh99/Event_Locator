package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.PreviousEventOrganizersProfileBinding
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate

class OrganizerPreviousEventAdapter(private val events: ArrayList<Event>):
        RecyclerView.Adapter<OrganizerPreviousEventAdapter.OrganizerPreviousEventHolder>() {
    lateinit var context: Context
    inner class OrganizerPreviousEventHolder(var binding: PreviousEventOrganizersProfileBinding):
            RecyclerView.ViewHolder(binding.root){
        init{
            //TODO: Set click lisetner
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizerPreviousEventHolder {
        val binding = PreviousEventOrganizersProfileBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        context = parent.context
        return OrganizerPreviousEventHolder(binding)
    }

    override fun onBindViewHolder(holder: OrganizerPreviousEventHolder, position: Int) {
        holder.binding.tvEventID.text = events[position].id.toString()
        holder.binding.tvEventName.text = events[position].name
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