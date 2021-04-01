package com.eventlocator.eventlocator.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.core.view.get
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.data.Organizer
import com.eventlocator.eventlocator.databinding.ActivityOrganizerProfileBinding
import com.eventlocator.eventlocator.retrofit.OrganizerService
import com.eventlocator.eventlocator.retrofit.ParticipantService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.SharedPreferenceManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.net.URLEncoder

class OrganizerProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrganizerProfileBinding
    lateinit var organizer: Organizer
    var organizerID:Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        organizerID = intent.getLongExtra("organizerID",-1)
        getAndLoadOrganizer()

    }

    override fun onResume() {
        super.onResume()
        getAndLoadOrganizer()
    }


    private fun getAndLoadOrganizer(){
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")

        RetrofitServiceFactory.createServiceWithAuthentication(OrganizerService::class.java, token!!)
                .getOrganizerById(organizerID).enqueue(object: Callback<Organizer>{
                    override fun onResponse(call: Call<Organizer>, response: Response<Organizer>) {
                        //TODO: Check response code
                        organizer = response.body()!!
                        organizer = response.body()!!
                        binding.tvOrgName.text = organizer.name
                        binding.tvAbout.text = organizer.about
                        binding.tvFollowers.text = organizer.numberOfFollowers.toString()
                        binding.tvEmail.text = organizer.email
                        setSocialMediaAccounts()
                        if (organizer.image!="") {
                            binding.ivOrgImage.setImageBitmap(BitmapFactory.decodeStream(
                                    ByteArrayInputStream(Base64.decode(organizer.image, Base64.DEFAULT))))
                        }
                        if(organizer.isFollowedByCurrentParticipant){
                            binding.btnFollowOrUnfollow.text = getString(R.string.unfollow)
                            binding.btnFollowOrUnfollow.setOnClickListener { unfollowOrganizer() }
                        }
                        else{
                            binding.btnFollowOrUnfollow.text = getString(R.string.follow)
                            binding.btnFollowOrUnfollow.setOnClickListener { followOrganizer() }
                        }
                    }

                    override fun onFailure(call: Call<Organizer>, t: Throwable) {
                        //TODO: Handle failure
                    }

                })
    }

    fun setSocialMediaAccounts(){
        for(i in 0 until organizer.socialMediaAccounts.size){
            if (organizer.socialMediaAccounts[i].accountName=="" && organizer.socialMediaAccounts[i].url==""){
                binding.llSocialMedia[i].visibility = View.GONE
            }
            else{
                when (i){
                    0 -> {
                        binding.llSocialMedia[i].setOnClickListener {
                            if (organizer.socialMediaAccounts[i].url!=""){
                                val intent = Intent(Intent.ACTION_VIEW,
                                        Uri.parse(organizer.socialMediaAccounts[i].url))
                                startActivity(intent)
                            }
                            else if (organizer.socialMediaAccounts[i].accountName!=""){
                                val url = "https://www.facebook.com/search/top?q="+
                                        URLEncoder.encode(organizer.socialMediaAccounts[i].accountName, "UTF-8")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }
                        }
                    }
                    1 -> {
                        binding.llSocialMedia[i].setOnClickListener {
                            if (organizer.socialMediaAccounts[i].url!=""){
                                val intent = Intent(Intent.ACTION_VIEW,
                                        Uri.parse(organizer.socialMediaAccounts[i].url))
                                startActivity(intent)
                            }
                            else if (organizer.socialMediaAccounts[i].accountName!=""){
                                val url = "https://www.youtube.com/results?search_query="+
                                        URLEncoder.encode(organizer.socialMediaAccounts[i].accountName, "UTF-8")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }
                        }
                    }
                    2 -> {
                        binding.llSocialMedia[i].setOnClickListener {
                            if (organizer.socialMediaAccounts[i].url!=""){
                                val intent = Intent(Intent.ACTION_VIEW,
                                        Uri.parse(organizer.socialMediaAccounts[i].url))
                                startActivity(intent)
                            }
                            else if (organizer.socialMediaAccounts[i].accountName!=""){
                                val url = "https://www.instagram.com/"+
                                        organizer.socialMediaAccounts[i].accountName
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }
                        }
                    }
                    3 -> {
                        binding.llSocialMedia[i].setOnClickListener {
                            if (organizer.socialMediaAccounts[i].url!=""){
                                val intent = Intent(Intent.ACTION_VIEW,
                                        Uri.parse(organizer.socialMediaAccounts[i].url))
                                startActivity(intent)
                            }
                            else if (organizer.socialMediaAccounts[i].accountName!=""){
                                val url = "https://twitter.com/search?q="+
                                        URLEncoder.encode(organizer.socialMediaAccounts[i].accountName, "UTF-8")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }
                        }
                    }
                    4 -> {
                        binding.llSocialMedia[i].setOnClickListener {
                            if (organizer.socialMediaAccounts[i].url!=""){
                                val intent = Intent(Intent.ACTION_VIEW,
                                        Uri.parse(organizer.socialMediaAccounts[i].url))
                                startActivity(intent)
                            }
                            else if (organizer.socialMediaAccounts[i].accountName!=""){
                                val url = "https://www.linkedin.com/search/results/all/?keywords="+
                                        URLEncoder.encode(organizer.socialMediaAccounts[i].accountName, "UTF-8")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
        if (organizer.socialMediaAccounts.size<5) binding.ivLinkedIn.visibility = View.GONE
    }

    fun followOrganizer(){
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        RetrofitServiceFactory.createServiceWithAuthentication(ParticipantService::class.java, token!!)
                .followOrganizer(organizerID).enqueue(object: Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        //TODO Handle success
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        //TODO: Handle failure
                    }

                })


    }

    fun unfollowOrganizer(){
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        RetrofitServiceFactory.createServiceWithAuthentication(ParticipantService::class.java, token!!)
                .unfollowOrganizer(organizerID).enqueue(object: Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        //TODO Handle success
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        //TODO: Handle failure
                    }

                })

    }
}