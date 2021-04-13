package com.eventlocator.eventlocator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.eventlocator.eventlocator.adapters.OrganizerAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.data.Organizer
import com.eventlocator.eventlocator.databinding.ActivityFollowedOrganizersBinding
import com.eventlocator.eventlocator.retrofit.ParticipantService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.SharedPreferenceManager
import com.eventlocator.eventlocator.utilities.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowedOrganizersActivity : AppCompatActivity() {
    lateinit var binding: ActivityFollowedOrganizersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowedOrganizersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutManager = LinearLayoutManager(this)
        binding.rvFollowedOrganizers.layoutManager = layoutManager
    }

    override fun onResume(){
        super.onResume()
        getAndLoadFollowedOrganizers()
    }

    private fun getAndLoadFollowedOrganizers(){
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        RetrofitServiceFactory.createServiceWithAuthentication(ParticipantService::class.java, token!!)
                .getFollowedOrganizers().enqueue(object: Callback<ArrayList<Organizer>> {
                    override fun onResponse(call: Call<ArrayList<Organizer>>, response: Response<ArrayList<Organizer>>) {
                        Toast.makeText(this@FollowedOrganizersActivity, response.code().toString(),Toast.LENGTH_LONG).show()
                        if(response.code()==202){
                            val adapter = OrganizerAdapter(response.body()!!)
                            binding.rvFollowedOrganizers.adapter = adapter
                            binding.rvFollowedOrganizers.adapter!!.notifyDataSetChanged()
                        }
                        else if (response.code()==401){
                            Utils.instance.displayInformationalDialog(this@FollowedOrganizersActivity, "Error",
                                    "401: Unauthorized access",true)
                        }
                        else if (response.code() == 404){
                            Utils.instance.displayInformationalDialog(this@FollowedOrganizersActivity,
                                    "Error", "No organizers found",false)
                        }
                        else if (response.code() == 500){
                            Utils.instance.displayInformationalDialog(this@FollowedOrganizersActivity,
                                    "Error", "Server issue, please try again later",false)
                        }

                    }

                    override fun onFailure(call: Call<ArrayList<Organizer>>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@FollowedOrganizersActivity,
                                "Error", "Can't connect to server",false)
                    }

                })
    }
}