package com.eventlocator.eventlocator.data.eventfilter

import com.eventlocator.eventlocator.data.Event

class RegistrationClosedFilter(var requiresRegistrationClosed: Boolean): Filter {

    override fun apply(events: ArrayList<Event>): ArrayList<Event> {
        if (requiresRegistrationClosed) return events
        else{
            val result = ArrayList<Event>()
            for(i in 0 until events.size){
                if (!events[i].isRegistrationClosed())
                    result.add(events[i])
            }
            return result
        }
    }
}