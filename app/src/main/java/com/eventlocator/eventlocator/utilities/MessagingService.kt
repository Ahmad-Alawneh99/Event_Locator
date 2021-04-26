package com.eventlocator.eventlocator.utilities

import android.content.Context
import android.util.Log
import android.widget.Toast
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
    }


    fun getToken(context: Context):String{
        return context.getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
            .getString(SharedPreferenceManager.instance.FIREASE_TOKEN_KEY, "EMPTY")!!
    }
}