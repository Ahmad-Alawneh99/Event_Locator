package com.eventlocator.eventlocator.data.eventfilter

import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.utilities.Utils

class EventNameFilter(var query: String): Filter {
    private val matchRate = 0.75
    override fun apply(events: ArrayList<Event>): ArrayList<Event> {
        val result = ArrayList<Event>()
        for(i in 0 until events.size){
            val length = Utils.instance.getLongestCommonSubsequenceLength(query, events[i].name)
            if (length.toDouble()/events[i].name.length >= matchRate)
                result.add(events[i])
        }
        return result
    }
}