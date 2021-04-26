package com.eventlocator.eventlocator.retrofit

import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.data.Feedback
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
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

    @POST("/participants/event/{id}/register")
    fun registerParticipantInEvent(@Path("id") eventID: Long, @Body token: ArrayList<String>): Call<ResponseBody>

    @POST("/participants/event/{id}/unregister")
    fun unregisterParticipantInEvent(@Path("id") eventID: Long, @Body token: ArrayList<String>): Call<ResponseBody>

    @POST("/{id}") //TODO: Specify actual router
    fun addParticipantRating(@Path("id") eventID: Long ,@Body feedback: Feedback): Call<ResponseBody>

}