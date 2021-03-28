package com.eventlocator.eventlocator.retrofit

import com.eventlocator.eventlocator.data.Event
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface EventService {

    @GET("/participants/upcomingEvents")
    fun getUpcomingEvents(): Call<ArrayList<Event>>

    @GET("/participants/upcomingEventsByFollowedOrganizers")
    fun getUpcomingEventsByFollowedOrganizers(): Call<ArrayList<Event>>

    @GET("/participants/event/{id}")
    fun getEventById(@Path("id") eventID: Long)
}