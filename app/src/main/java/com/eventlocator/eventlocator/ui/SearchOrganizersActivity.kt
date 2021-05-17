package com.eventlocator.eventlocator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.eventlocator.eventlocator.adapters.OrganizerAdapter
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.data.Organizer
import com.eventlocator.eventlocator.data.eventfilter.OrganizerNameFilter
import com.eventlocator.eventlocator.databinding.ActivitySearchOrganizersBinding
import com.eventlocator.eventlocator.retrofit.OrganizerService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.SharedPreferenceManager
import com.eventlocator.eventlocator.utilities.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchOrganizersActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchOrganizersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchOrganizersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivSearch.setOnClickListener {
            if (binding.etOrganizerName.text.toString().trim() == ""){
                Toast.makeText(this, "Please provide a value to search", Toast.LENGTH_LONG).show()
            }
            else{
                searchOrganizers(binding.etOrganizerName.text.toString().trim())
            }
        }
    }

    private fun searchOrganizers(query: String){
        binding.pbLoading.visibility = View.VISIBLE
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        RetrofitServiceFactory.createServiceWithAuthentication(OrganizerService::class.java, token!!)
                .getOrganizers().enqueue(object : Callback<ArrayList<Organizer>> {
                    override fun onResponse(call: Call<ArrayList<Organizer>>, response: Response<ArrayList<Organizer>>) {
                        if (response.code() == 200) {
                            val result = filterResult(response.body()!!, query)
                            val layoutManager = LinearLayoutManager(this@SearchOrganizersActivity,
                                    LinearLayoutManager.VERTICAL, false)
                            binding.rvOrganizers.layoutManager = layoutManager
                            if (result.size == 0) {
                                Toast.makeText(this@SearchOrganizersActivity, "No matching organizers found", Toast.LENGTH_SHORT).show()
                                binding.rvOrganizers.adapter = null
                            }
                            else {
                                val adapter = OrganizerAdapter(result)
                                binding.rvOrganizers.adapter = adapter
                            }
                        }
                        else if (response.code() == 401) {
                            Utils.instance.displayInformationalDialog(this@SearchOrganizersActivity,
                                    "Error", "401: Unauthorized access", true)
                        }
                        else if (response.code() == 404) {
                            Utils.instance.displayInformationalDialog(this@SearchOrganizersActivity,
                                    "Error", "No organizers found", false)
                        }
                        else if (response.code() == 500) {
                            Utils.instance.displayInformationalDialog(this@SearchOrganizersActivity,
                                    "Error", "Server issue, please try again later", true)
                        }
                        binding.pbLoading.visibility = View.INVISIBLE
                    }

                    override fun onFailure(call: Call<ArrayList<Organizer>>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@SearchOrganizersActivity,
                                "Error", "Can't connect to server", true)
                        binding.pbLoading.visibility = View.INVISIBLE
                    }

                })
    }

    private fun filterResult(organizers: ArrayList<Organizer>, query: String): ArrayList<Organizer>{
        val matchRate = 0.75
        val result = ArrayList<Organizer>()
        for(i in 0 until organizers.size){
            val length = Utils.instance.getLongestCommonSubsequenceLength(query.toLowerCase(), organizers[i].name.toLowerCase())
            if (length.toDouble()/organizers[i].name.length >= matchRate) {
                result.add(organizers[i])
            }
        }
        return result
    }
}