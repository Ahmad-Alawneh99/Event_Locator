package com.eventlocator.eventlocator.data.eventfilter

import com.eventlocator.eventlocator.data.Event
import java.time.DayOfWeek
import java.util.*
import kotlin.collections.ArrayList

class DayOfWeekFilter(var days: ArrayList<Int>): Filter {
    override fun apply(events: ArrayList<Event>): ArrayList<Event> {
        val result = ArrayList<Event>()
        for(i in 0 until events.size){
            for(j in 0 until events[i].sessions.size) {
                if (days.contains(events[i].sessions[j].dayOfWeek)) {
                    result.add(events[i])
                    break
                }
            }
        }
        return result
    }
}