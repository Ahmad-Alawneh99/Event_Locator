package com.eventlocator.eventlocator.retrofit

import com.eventlocator.eventlocator.data.Participant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ParticipantService {

    @POST("/participants/signup")
    fun createParticipant(@Body participant: Participant): Call<ResponseBody>

    @POST("/participants/login")
    fun login(@Body credentials: ArrayList<String>): Call<String>

    @POST("/participants/signup/partial")
    fun checkIfExists(@Body email: String)

    @POST("/participant/follow/organizer/{id}")
    fun followOrganizer(@Path("id") organizerID: Long): Call<ResponseBody>

    @POST("/participant/unfollow/organizer/{id}")
    fun unfollowOrganizer(@Path("id") organizerID: Long): Call<ResponseBody>

}