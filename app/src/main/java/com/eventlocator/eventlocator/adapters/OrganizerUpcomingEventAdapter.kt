package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.UpcomingEventOrganizersProfileBinding
import com.eventlocator.eventlocator.ui.ViewEventActivity
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate

class OrganizerUpcomingEventAdapter(private val events: ArrayList<Event>):
        RecyclerView.Adapter<OrganizerUpcomingEventAdapter.OrganizerUpcomingEventHolder>() {
    lateinit var context: Context
    inner class OrganizerUpcomingEventHolder(var binding: UpcomingEventOrganizersProfileBinding):
            RecyclerView.ViewHolder(binding.root){

        init{
            binding.root.setOnClickListener {
                val intent = Intent(context, ViewEventActivity::class.java)
                intent.putExtra("eventID", binding.tvEventID.text.toString().toLong())
                context.startActivity(intent)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizerUpcomingEventHolder {
        val binding = UpcomingEventOrganizersProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return OrganizerUpcomingEventHolder(binding)
    }

    override fun onBindViewHolder(holder: OrganizerUpcomingEventHolder, position: Int) {
        holder.binding.tvEventID.text = events[position].id.toString()
        holder.binding.tvEventName.text = events[position].name
        val startDate = LocalDate.parse(events[position].startDate,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        val endDate = LocalDate.parse(events[position].endDate,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        holder.binding.tvEventDates.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                .format(startDate) + " - " +
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY).format(endDate)
        val status = events[position].getStatus()
        holder.binding.tvEventStatus.text = status
        if (status == "Full" || status == "Registration closed"){
            holder.binding.tvEventStatus.setTextColor(ContextCompat.getColor(context, R.color.design_default_color_error))
        }
        else if (status == "Pending (waiting for response from admins)"){
            holder.binding.tvEventStatus.setTextColor(ContextCompat.getColor(context, R.color.warning))
        }
        else{
            holder.binding.tvEventStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
        }
    }

    override fun getItemCount(): Int = events.size
}