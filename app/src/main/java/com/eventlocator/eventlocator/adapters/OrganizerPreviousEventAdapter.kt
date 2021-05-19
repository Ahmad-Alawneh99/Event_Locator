package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.PreviousEventOrganizersProfileBinding
import com.eventlocator.eventlocator.ui.ViewEventActivity
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.math.BigDecimal
import java.time.LocalDate

class OrganizerPreviousEventAdapter(private val events: ArrayList<Event>):
        RecyclerView.Adapter<OrganizerPreviousEventAdapter.OrganizerPreviousEventHolder>() {
    lateinit var context: Context
    inner class OrganizerPreviousEventHolder(var binding: PreviousEventOrganizersProfileBinding):
            RecyclerView.ViewHolder(binding.root){
        init{
            binding.root.setOnClickListener {
                val intent = Intent(context, ViewEventActivity::class.java)
                intent.putExtra("eventID", binding.tvEventID.text.toString().toLong())
                context.startActivity(intent)
            }
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

        holder.binding.tvEventRating.text = if (events[position].rating>0.0) BigDecimal(events[position].rating).setScale(2).toString() + "/5" else "No ratings yet"
    }

    override fun getItemCount(): Int = events.size
}