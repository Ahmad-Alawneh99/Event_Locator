package com.eventlocator.eventlocator.utilities

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.ui.ViewEventActivity
import com.eventlocator.eventlocator.utilities.NotificationUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
            .edit().putString(SharedPreferenceManager.instance.FIREASE_TOKEN_KEY, p0).apply()
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.e("MESSAGE", "REceived")
        val notification = NotificationUtils.createNotification(this, NotificationUtils.UPDATES_CHANNEL_ID,
                p0.data["title"]!!, p0.data["message"]!!, R.drawable.ic_info)

        val actionIntent = Intent(this, ViewEventActivity::class.java)
        actionIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        actionIntent.putExtra("eventID", p0.data["eventID"]!!.toLong())
        val actionPendingIntent = PendingIntent.getActivity(this, p0.data["eventID"]!!.toInt(), actionIntent, 0)

        notification.setContentIntent(actionPendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(p0.data["eventID"]!!.toInt(), notification.build())
    }


    fun getToken(context: Context):String{
        return context.getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
            .getString(SharedPreferenceManager.instance.FIREASE_TOKEN_KEY, "EMPTY")!!
    }
}