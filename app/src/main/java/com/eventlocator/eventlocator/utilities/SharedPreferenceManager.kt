package com.eventlocator.eventlocator.utilities

class SharedPreferenceManager {
    val SHARED_PREFERENCE_FILE = "com.eventlocator.eventlocator"
    val TOKEN_KEY = "Token"
    val FIRST_TIME_KEY = "FirstTime?"
    val PARTICIPANT_ID_KEY = "participant"
    val FIREASE_TOKEN_KEY = "firebase"
    companion object{
        val instance: SharedPreferenceManager = SharedPreferenceManager()
    }

}