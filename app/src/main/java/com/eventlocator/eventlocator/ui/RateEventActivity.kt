package com.eventlocator.eventlocator.ui

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.eventlocator.eventlocator.data.Feedback
import com.eventlocator.eventlocator.databinding.ActivityRateEventBinding
import com.eventlocator.eventlocator.retrofit.EventService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.SharedPreferenceManager
import com.eventlocator.eventlocator.utilities.Utils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RateEventActivity : AppCompatActivity() {
    lateinit var binding: ActivityRateEventBinding
    var eventID: Long = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        eventID = intent.getLongExtra("eventID", -1)
        binding.btnSave.isEnabled = false
        binding.etFeedback.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etFeedback.text.toString().trim() == ""){
                    binding.tlFeedback.error = "Field can't be empty"
                }
                else if (Utils.instance.countWords(binding.etFeedback.text.toString().trim()) > 500){
                    binding.tlFeedback.error = "Only 500 words are allowed"
                }
                else if (binding.etFeedback.text.toString().length == 65535){
                    binding.tlFeedback.error = "Max number of characters reached (65535)"
                }
                else{
                    binding.tlFeedback.error = null
                }
                updateSaveButton()
            }

        })
        binding.etFeedback.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) binding.etFeedback.setText(
                    Utils.instance.connectWordsIntoString(binding.etFeedback.text.toString().trim().split(' ')))
            updateSaveButton()
        }


        binding.rbRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            updateSaveButton()
        }

        binding.btnSave.setOnClickListener {
            val dialogAlert = Utils.instance.createSimpleDialog(this, "Rate", "Are you sure that you want to submit this rating for this event?")
            dialogAlert.setPositiveButton("Yes"){di: DialogInterface, i: Int ->
                binding.pbLoading.visibility = View.VISIBLE
                val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                        .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")

                val feedback = Feedback(binding.rbRating.rating.toDouble(), binding.etFeedback.text.toString().trim())

                RetrofitServiceFactory.createServiceWithAuthentication(EventService::class.java, token!!)
                        .addParticipantRating(eventID, feedback).enqueue(object: Callback<ResponseBody> {

                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if (response.code() == 200){
                                    Utils.instance.displayInformationalDialog(this@RateEventActivity,
                                            "Success", "Rating added successfully", true)
                                }
                                else if (response.code() == 401) {
                                    Utils.instance.displayInformationalDialog(this@RateEventActivity,
                                            "Error", "401: Unauthorized access", true)
                                }
                                else if (response.code() == 406) {
                                    Utils.instance.displayInformationalDialog(this@RateEventActivity,
                                            "Error", "Participant or event not found", true)
                                }
                                else if (response.code() == 409) {
                                    Utils.instance.displayInformationalDialog(this@RateEventActivity,
                                            "Error", "Already added feedback", true)
                                }
                                else if (response.code() == 500) {
                                    Utils.instance.displayInformationalDialog(this@RateEventActivity,
                                            "Error", "Server issue, please try again later", false)
                                }
                                binding.pbLoading.visibility = View.INVISIBLE
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Utils.instance.displayInformationalDialog(this@RateEventActivity,
                                        "Error", "Can't connect to server", true)
                                binding.pbLoading.visibility = View.INVISIBLE
                            }

                        })
            }
            dialogAlert.setNegativeButton("No"){di: DialogInterface, i: Int -> }
            dialogAlert.create().show()

        }

    }



    private fun updateSaveButton(){
        binding.btnSave.isEnabled = binding.rbRating.rating > 0.0
                && binding.tlFeedback.error == null
                && binding.etFeedback.text.toString().trim() != ""
    }
}