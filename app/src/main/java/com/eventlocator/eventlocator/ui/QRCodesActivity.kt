package com.eventlocator.eventlocator.ui

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eventlocator.eventlocator.adapters.QRCodeAdapter
import com.eventlocator.eventlocator.data.Session
import com.eventlocator.eventlocator.databinding.ActivityQRCodesBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class QRCodesActivity : AppCompatActivity() {
    lateinit var binding: ActivityQRCodesBinding
    var participantID: Long = -1
    var eventID: Long = -1
    lateinit var sessions: ArrayList<Session>;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQRCodesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()

        val qrCodes = ArrayList<Bitmap>()

        for(i in 0 until sessions.size){
            val hintMap = mapOf(EncodeHintType.MARGIN to 2)
            val multiFormatWriter = MultiFormatWriter()
            val data = participantID.toString()+ ","+eventID.toString()+","+sessions[i].id
            val bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, 400, 400, hintMap)
            val barcodeEncoder = BarcodeEncoder()
            qrCodes.add(barcodeEncoder.createBitmap(bitMatrix))
        }

        val layoutManager = LinearLayoutManager(this)
        val adapter = QRCodeAdapter(sessions, qrCodes)

        binding.rvQRCodes.layoutManager = layoutManager
        binding.rvQRCodes.adapter = adapter

    }


    private fun getData(){
        participantID = intent.getLongExtra("participantID",-1)
        eventID = intent.getLongExtra("eventID", -1)
        sessions = (intent.getSerializableExtra("sessions") as ArrayList<Session>)
    }
}