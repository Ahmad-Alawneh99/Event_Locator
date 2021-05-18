package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.UpcomingEventBinding
import com.eventlocator.eventlocator.ui.ViewEventActivity
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate

class UpcomingEventAdapter(var events: ArrayList<Event>,):
        RecyclerView.Adapter<UpcomingEventAdapter.UpcomingEventHolder>() {
    lateinit var context: Context
    inner class UpcomingEventHolder(val binding: UpcomingEventBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.root.setOnClickListener {
                val intent = Intent(context, ViewEventActivity::class.java)
                intent.putExtra("eventID", binding.tvEventID.text.toString().toLong())
                context.startActivity(intent)
            }
        }
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
        val status = events[position].getStatus()
        holder.binding.tvEventStatus.text = status
        if (status == "This event is full" || status == "Registration closed"){
            holder.binding.tvEventStatus.setTextColor(ContextCompat.getColor(context, R.color.design_default_color_error))
        }
        else{
            holder.binding.tvEventStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

}