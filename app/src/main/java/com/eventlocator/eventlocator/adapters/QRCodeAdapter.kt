package com.eventlocator.eventlocator.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.data.Session
import com.eventlocator.eventlocator.databinding.QrCodeItemBinding
import com.eventlocator.eventlocator.ui.ViewImageActivity
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.io.ByteArrayOutputStream
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class QRCodeAdapter(private val sessions: ArrayList<Session>, private val qrCodes: ArrayList<Bitmap>):
    RecyclerView.Adapter<QRCodeAdapter.QRCodeViewHolder>() {
    lateinit var context: Context
    inner class QRCodeViewHolder(val binding: QrCodeItemBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.ivQRCode.setOnClickListener {
                val imageBitmap = binding.ivQRCode.drawable.toBitmap()
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val imageBase64 = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
                val intent = Intent(context, ViewImageActivity::class.java)
                intent.putExtra("image", imageBase64)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QRCodeViewHolder {
        val binding = QrCodeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return QRCodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QRCodeViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.binding.tvSessionNumber.text = "Session #"+sessions[position].id
        holder.binding.tvSessionDay.text = DayOfWeek.of(sessions[position].dayOfWeek).name
        val date = LocalDate.parse(sessions[position].date, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        holder.binding.tvSessionDate.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
            .format(date)

        holder.binding.ivQRCode.setImageBitmap(qrCodes[position])


    }

    override fun getItemCount(): Int = sessions.size
}