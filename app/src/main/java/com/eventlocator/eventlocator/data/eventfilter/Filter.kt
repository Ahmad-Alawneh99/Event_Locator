package com.eventlocator.eventlocator.data.eventfilter

import com.eventlocator.eventlocator.data.Event

interface Filter {

    fun apply(events: ArrayList<Event>): ArrayList<Event>
}