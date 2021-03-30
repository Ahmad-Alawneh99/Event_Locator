package com.eventlocator.eventlocator.retrofit

import com.eventlocator.eventlocator.data.Event
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventService {

    @GET("/participants/events/upcoming")
    fun getUpcomingEvents(): Call<ArrayList<Event>>

    @GET("/participants/events/upcomingByFollowedOrganizers")
    fun getUpcomingEventsByFollowedOrganizers(): Call<ArrayList<Event>>

    @GET("/participants/event/{id}")
    fun getEventById(@Path("id") eventID: Long): Call<Event>

    @GET("/participants/events")
    fun getParticipantEvents(): Call<ArrayList<Event>>

    @POST("/participants/event/register/{id}")
    fun registerParticipantInEvent(@Path("id") eventID: Long): Call<ResponseBody>

    @POST("/participants/event/unregister/{id}")
    fun unregisterParticipantInEvent(@Path("id") eventID: Long): Call<ResponseBody>

}