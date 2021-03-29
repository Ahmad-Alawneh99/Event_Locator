package com.eventlocator.eventlocator.retrofit

import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.data.Organizer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OrganizerService {

    @GET("/participants/organizers/getAll")
    fun getOrganizers(): Call<ArrayList<Organizer>>

    @GET("/participants/organizer/{id}")
    fun getOrganizerById(@Path("id") organizerID: Long): Call<Organizer>

    @GET("/participants/organizer/{id}/events")
    fun getOrganizerEvents(@Path("id") organizerID: Long): Call<ArrayList<Event>>
}