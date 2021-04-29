package com.eventlocator.eventlocator.retrofit

import com.eventlocator.eventlocator.data.Organizer
import com.eventlocator.eventlocator.data.Participant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ParticipantService {

    @POST("/participants/signup")
    fun createParticipant(@Body participant: Participant): Call<ResponseBody>

    @POST("/participants/login")
    fun login(@Body credentials: ArrayList<String>): Call<String>

    @POST("/participants/signup/partial")
    fun checkIfExists(@Body email: String)

    @POST("/participants/follow/organizer/{id}")
    fun followOrganizer(@Path("id") organizerID: Long): Call<ResponseBody>

    @POST("/participants/unfollow/organizer/{id}")
    fun unfollowOrganizer(@Path("id") organizerID: Long): Call<ResponseBody>

    @GET("/participants/organizers/followed")
    fun getFollowedOrganizers(): Call<ArrayList<Organizer>>

    @GET("/participant/information")
    fun getParticipantInfo(): Call<Participant>

    @PATCH("/participants/updateEmail")
    fun updateParticipantEmail(@Body data: ArrayList<String>): Call<String>

    @PATCH("/participants/changePassword")
    fun changeParticipantPassword(@Body data: ArrayList<String>): Call<ResponseBody>

    @PATCH("/participants/editCityAndCategories")
    fun editParticipantCityAndCategories(@Body participant: Participant): Call<ResponseBody>

}