package com.eventlocator.eventlocator.ui

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.data.Participant
import com.eventlocator.eventlocator.databinding.ActivityEditProfileBinding
import com.eventlocator.eventlocator.retrofit.ParticipantService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.EventCategory
import com.eventlocator.eventlocator.utilities.SharedPreferenceManager
import com.eventlocator.eventlocator.utilities.Utils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    lateinit var participant: Participant
    lateinit var cities: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cities = listOf(getString(R.string.Amman),getString(R.string.Zarqa),getString(R.string.Balqa)
                ,getString(R.string.Madaba),getString(R.string.Irbid),getString(R.string.Mafraq)
                ,getString(R.string.Jerash),getString(R.string.Ajloun),getString(R.string.Karak)
                ,getString(R.string.Aqaba),getString(R.string.Maan),getString(R.string.Tafila))

        participant = intent.getSerializableExtra("participant") as Participant
        loadParticipant()

        binding.btnSaveChanges.setOnClickListener {
            val dialogAlert = Utils.instance.createSimpleDialog(this, "Edit profile",
            "Are you sure that you want to save these changes?")
            dialogAlert.setPositiveButton("Yes"){di: DialogInterface, i: Int ->
                binding.btnSaveChanges.isEnabled = false
                binding.pbLoading.visibility = View.VISIBLE

                val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                        .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")

                val categories = ArrayList<Int>()
                if (binding.cbEducational.isChecked) categories.add(EventCategory.EDUCATIONAL.ordinal)
                if (binding.cbEntertainment.isChecked) categories.add(EventCategory.ENTERTAINMENT.ordinal)
                if (binding.cbVolunteering.isChecked) categories.add(EventCategory.VOLUNTEERING.ordinal)
                if (binding.cbSports.isChecked) categories.add(EventCategory.SPORTS.ordinal)

                val city = cities.indexOf(binding.acCityMenu.text.toString())

                val newParticipant = Participant("WILL NOT BE USED",
                        "WILL NOT BE USED",
                        "WILL NOT BE USED",
                        "WILL NOT BE USED",
                        0.0,
                        city,
                        categories)

                RetrofitServiceFactory.createServiceWithAuthentication(ParticipantService::class.java, token!!)
                        .editParticipantCityAndCategories(newParticipant).enqueue(object: Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if (response.code() == 200){
                                    Utils.instance.displayInformationalDialog(this@EditProfileActivity, "Success",
                                            "Changes saved",true)
                                }
                                else if (response.code() == 500){
                                    Utils.instance.displayInformationalDialog(this@EditProfileActivity, "Error",
                                            "Server issue, please try again later",false)
                                }
                                binding.btnSaveChanges.isEnabled = true
                                binding.pbLoading.visibility = View.INVISIBLE
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Utils.instance.displayInformationalDialog(this@EditProfileActivity, "Error",
                                        "Can't connect to server",false)
                                binding.btnSaveChanges.isEnabled = true
                                binding.pbLoading.visibility = View.INVISIBLE
                            }

                        })
            }
            dialogAlert.setNegativeButton("No"){di:DialogInterface, i:Int ->}
            dialogAlert.create().show()
        }

        binding.btnUpdateEmail.setOnClickListener {
            startActivity(Intent(this, UpdateEmailActivity::class.java))
        }

        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        val adapter = ArrayAdapter(this, R.layout.city_list_item, cities)
        binding.acCityMenu.setAdapter(adapter)

        binding.cbEducational.setOnCheckedChangeListener { buttonView, isChecked ->
            updateEventCategoryStatus()
        }
        binding.cbEntertainment.setOnCheckedChangeListener { buttonView, isChecked ->
            updateEventCategoryStatus()
        }
        binding.cbVolunteering.setOnCheckedChangeListener { buttonView, isChecked ->
            updateEventCategoryStatus()
        }
        binding.cbSports.setOnCheckedChangeListener { buttonView, isChecked ->
            updateEventCategoryStatus()
        }
        
    }

    private fun loadParticipant(){
        binding.acCityMenu.setText(cities[participant.city], TextView.BufferType.EDITABLE)
        for(i in 0 until participant.preferredEventCategories.size){
            when(participant.preferredEventCategories[i]){
                EventCategory.EDUCATIONAL.ordinal -> {
                    binding.cbEducational.isChecked = true
                }
                EventCategory.ENTERTAINMENT.ordinal -> {
                    binding.cbEntertainment.isChecked = true
                }
                EventCategory.VOLUNTEERING.ordinal -> {
                    binding.cbVolunteering.isChecked = true
                }
                EventCategory.SPORTS.ordinal -> {
                    binding.cbSports.isChecked = true
                }
            }
        }
        updateEventCategoryStatus()
    }

    private fun updateEventCategoryStatus(){
        binding.tvEventCategoryError.visibility = if (!(binding.cbEducational.isChecked || binding.cbEntertainment.isChecked ||
                        binding.cbVolunteering.isChecked || binding.cbSports.isChecked)) View.VISIBLE else View.INVISIBLE
        updateSaveButton()
    }

    private fun updateSaveButton(){
        binding.btnSaveChanges.isEnabled =
                cities.contains(binding.acCityMenu.text.toString())
                && binding.tvEventCategoryError.visibility == View.INVISIBLE
    }
}