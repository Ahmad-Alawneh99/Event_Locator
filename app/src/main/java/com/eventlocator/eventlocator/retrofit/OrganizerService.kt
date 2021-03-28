package com.eventlocator.eventlocator.retrofit

import com.eventlocator.eventlocator.data.Organizer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OrganizerService {

    @GET("/organizers/getAll")
    fun getOrganizers(): Call<ArrayList<Organizer>>

    @GET("/organizer/{id}")
    fun getOrganizerById(@Path("id") organizerID: Long): Call<Organizer>
}