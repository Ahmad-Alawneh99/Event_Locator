package com.eventlocator.eventlocator.utilities

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.ui.ViewEventActivity
import java.time.LocalDateTime
import java.time.ZoneId

class NotificationUtils {

    companion object{
        private val REMINDERS_CHANNEL_ID = "reminders_channel_id_participants_app"
        val UPDATES_CHANNEL_ID = "updates_channel_id_participants_app"

        private fun createNotificationChannels(context: Context){
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val remindersChannel = NotificationChannel(REMINDERS_CHANNEL_ID, "Reminders", NotificationManager.IMPORTANCE_HIGH)
            remindersChannel.description = "This channel is for all reminders"
            remindersChannel.enableVibration(true)
            notificationManager.createNotificationChannel(remindersChannel)

            val updatesChannel = NotificationChannel(UPDATES_CHANNEL_ID, "Updates", NotificationManager.IMPORTANCE_HIGH)
            updatesChannel.description = "This channel is for all updates related to events (when events get canceled or modified"
            updatesChannel.enableVibration(true)
            notificationManager.createNotificationChannel(updatesChannel)
        }

        fun createNotification(context: Context, channelID: String, title: String, message: String, icon: Int): Notification.Builder{
            createNotificationChannels(context)
            return Notification.Builder(context, channelID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(icon)
                    .setAutoCancel(true)
        }

        fun scheduleNotification(context: Context, sendDateTime: LocalDateTime, notificationID: Int, content: String, eventID: Long){
            createNotificationChannels(context)
            val alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val notificationReceiver = NotificationBroadcastReceiver()
            val filter =  IntentFilter("NOTIFICATION_ACTION");
            context.registerReceiver(notificationReceiver, filter)

            val notificationBuilder = createNotification(context, REMINDERS_CHANNEL_ID, "Reminder", content, R.drawable.ic_clock)
            val actionIntent = Intent(context, ViewEventActivity::class.java)
            actionIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            actionIntent.putExtra("eventID", eventID)
            val actionPendingIntent = PendingIntent.getActivity(context, eventID.toInt(), actionIntent, 0)
            notificationBuilder.setContentIntent(actionPendingIntent)

            val notificationIntent = Intent("NOTIFICATION_ACTION")
            notificationIntent.putExtra("notification", notificationBuilder.build())
            notificationIntent.putExtra("notificationID", notificationID)
            val notificationPendingIntent = PendingIntent.getBroadcast(context.applicationContext, eventID.toInt(), notificationIntent, 0)
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    sendDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),notificationPendingIntent)
        }

        fun cancelNotification(context: Context, eventID: Long){
            val intent = Intent("NOTIFICATION_ACTION")
            val pendingIntent = PendingIntent.getBroadcast(context.applicationContext, eventID.toInt(),intent, PendingIntent.FLAG_NO_CREATE)
            val alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (pendingIntent!=null){
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

    }



}