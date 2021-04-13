package com.eventlocator.eventlocator.data.eventfilter

import android.util.Log
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.utilities.Utils

class OrganizerNameFilter(var query: String): Filter {
    private val matchRate = 0.75
    override fun apply(events: ArrayList<Event>): ArrayList<Event> {
        val result = ArrayList<Event>()
        for(i in 0 until events.size){
            val length = Utils.instance.getLongestCommonSubsequenceLength(query.toLowerCase(), events[i].organizerName.toLowerCase())
            if (length.toDouble()/events[i].organizerName.length >= matchRate) {
                result.add(events[i])
            }
        }
        return result
    }
}