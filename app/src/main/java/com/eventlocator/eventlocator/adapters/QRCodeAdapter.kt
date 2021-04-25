package com.eventlocator.eventlocator.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.data.Session
import com.eventlocator.eventlocator.databinding.QrCodeItemBinding
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.time.DayOfWeek
import java.time.LocalDate

class QRCodeAdapter(private val sessions: ArrayList<Session>, private val qrCodes: ArrayList<Bitmap>):
    RecyclerView.Adapter<QRCodeAdapter.QRCodeViewHolder>() {

    inner class QRCodeViewHolder(val binding: QrCodeItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QRCodeViewHolder {
        val binding = QrCodeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QRCodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QRCodeViewHolder, position: Int) {
        holder.binding.tvSessionNumber.text = "Session #"+sessions[position].id
        holder.binding.tvSessionDay.text = DayOfWeek.of(sessions[position].dayOfWeek).name
        val date = LocalDate.parse(sessions[position].date, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        holder.binding.tvSessionDate.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
            .format(date)

        holder.binding.ivQRCode.setImageBitmap(qrCodes[position])


    }

    override fun getItemCount(): Int = sessions.size
}