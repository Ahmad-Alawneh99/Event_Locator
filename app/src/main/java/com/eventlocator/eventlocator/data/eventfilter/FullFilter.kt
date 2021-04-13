package com.eventlocator.eventlocator.data.eventfilter

import com.eventlocator.eventlocator.data.Event

class FullFilter(var requiresFull: Boolean): Filter {
    override fun apply(events: ArrayList<Event>): ArrayList<Event> {
        if (requiresFull) return events
        else{
            val result = ArrayList<Event>()
            for(i in 0 until events.size){
                if (!events[i].isFull()){
                    result.add(events[i])
                }
            }
            return result
        }
    }
}