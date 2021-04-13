package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.databinding.CanceledEventParticipantsProfileBinding
import com.eventlocator.eventlocator.ui.ViewEventActivity
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.LocalDate
import java.time.LocalDateTime

class ParticipantCanceledEventAdapter(private val events: ArrayList<Event>):
        RecyclerView.Adapter<ParticipantCanceledEventAdapter.ParticipantCanceledEventHolder>() {

    lateinit var context: Context

    inner class ParticipantCanceledEventHolder(var binding: CanceledEventParticipantsProfileBinding):
            RecyclerView.ViewHolder(binding.root){

        init{
            binding.root.setOnClickListener {
                val intent = Intent(context, ViewEventActivity::class.java)
                intent.putExtra("eventID", binding.tvEventID.text.toString().toLong())
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantCanceledEventHolder {
        val binding = CanceledEventParticipantsProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ParticipantCanceledEventHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantCanceledEventHolder, position: Int) {
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

        val cancellationDateTime: LocalDateTime = LocalDateTime.parse(events[position].canceledEventData!!.cancellationDateTime,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))
        holder.binding.tvCancellationDateTime.text =
                "Canceled on: " + DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DISPLAY)
                        .format(cancellationDateTime)
    }

    override fun getItemCount(): Int = events.size

}