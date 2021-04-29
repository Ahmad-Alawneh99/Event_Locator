package com.eventlocator.eventlocator.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.data.Participant
import com.eventlocator.eventlocator.databinding.ActivitySignUpBinding
import com.eventlocator.eventlocator.retrofit.ParticipantService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.EventCategory
import com.eventlocator.eventlocator.utilities.Utils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var cities: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cities = listOf(getString(R.string.Amman),getString(R.string.Zarqa),getString(R.string.Balqa)
            ,getString(R.string.Madaba),getString(R.string.Irbid),getString(R.string.Mafraq)
            ,getString(R.string.Jerash),getString(R.string.Ajloun),getString(R.string.Karak)
            ,getString(R.string.Aqaba),getString(R.string.Maan),getString(R.string.Tafila))

        binding.btnSignUp.isEnabled = false
        binding.tlCityMenu.error = "City is required"
        binding.btnSignUp.setOnClickListener {
            binding.pbLoading.visibility = View.VISIBLE
            binding.btnSignUp.isEnabled = false

            val categories = ArrayList<Int>()
            if (binding.cbEducational.isChecked) categories.add(EventCategory.EDUCATIONAL.ordinal)
            if (binding.cbEntertainment.isChecked) categories.add(EventCategory.ENTERTAINMENT.ordinal)
            if (binding.cbVolunteering.isChecked) categories.add(EventCategory.VOLUNTEERING.ordinal)
            if (binding.cbSports.isChecked) categories.add(EventCategory.SPORTS.ordinal)

            val city = cities.indexOf(binding.acCityMenu.text.toString())

            val participant = Participant(binding.etEmail.text.toString(),
                binding.etFirstName.text.toString(),
                binding.etLastName.text.toString(),
                binding.etPassword.text.toString(),
                5.0,
                city,
                categories)


            RetrofitServiceFactory.createService(ParticipantService::class.java)
                .createParticipant(participant).enqueue(object: Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.code() == 201){
                            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                        }
                        else if (response.code() == 409){
                            Utils.instance.displayInformationalDialog(this@SignUpActivity, "Error",
                                    "Email already exists, please use a different email",false)
                        }
                        else if (response.code()==500){
                            Utils.instance.displayInformationalDialog(this@SignUpActivity, "Error",
                                    "Server issue, please try again later",false)
                        }
                        binding.pbLoading.visibility = View.INVISIBLE
                        binding.btnSignUp.isEnabled = true
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@SignUpActivity, "Error",
                                "Can't connect to the server",false)
                        binding.pbLoading.visibility = View.INVISIBLE
                        binding.btnSignUp.isEnabled = true

                    }

                })
        }

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etEmail.text.toString().trim() == "") {
                    binding.tlEmail.error = getString(R.string.email_cant_be_empty_error)
                } else if (Utils.instance.isEmail(binding.etEmail.text.toString().trim())) {
                    binding.tlEmail.error = null
                } else {
                    binding.tlEmail.error = getString(R.string.invalid_email_error)
                }
                updateSignUpButton()
            }

        })

        binding.etEmail.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.etEmail.setText(
                    binding.etEmail.text.toString().trim(),
                    TextView.BufferType.EDITABLE
                )
                updateSignUpButton()
            }
        }

        binding.etFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etFirstName.text.toString().trim() == "") {
                    binding.tlFirstName.error = getString(R.string.first_name_empty_error)
                } else if (binding.etFirstName.text.toString().trim().length > 16) {
                    binding.tlFirstName.error = getString(R.string.first_name_max_length_error)
                } else {
                    var hasNumbers = false
                    for (i in binding.etFirstName.text.toString().trim().indices) {
                        if (binding.etFirstName.text.toString().trim()[i] in '0'..'9') {
                            hasNumbers = true
                            break
                        }
                    }
                    if (hasNumbers) {
                        binding.tlFirstName.error = getString(R.string.name_contains_numbers_error)
                    } else {
                        binding.tlFirstName.error = null
                    }
                }
                updateSignUpButton()
            }

        })

        binding.etFirstName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.etFirstName.setText(
                    binding.etFirstName.text.toString().trim(),
                    TextView.BufferType.EDITABLE
                )
                updateSignUpButton()
            }
        }

        binding.etLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etLastName.text.toString().trim() == "") {
                    binding.tlLastName.error = getString(R.string.last_name_empty_error)
                } else if (binding.etLastName.text.toString().trim().length > 16) {
                    binding.tlLastName.error = getString(R.string.last_name_max_length_error)
                } else {
                    var hasNumbers = false
                    for (i in binding.etLastName.text.toString().trim().indices) {
                        if (binding.etLastName.text.toString().trim()[i] in '0'..'9') {
                            hasNumbers = true
                            break
                        }
                    }
                    if (hasNumbers) {
                        binding.tlLastName.error = getString(R.string.name_contains_numbers_error)
                    } else {
                        binding.tlLastName.error = null
                    }
                }
                updateSignUpButton()
            }

        })

        binding.etLastName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.etLastName.setText(
                    binding.etLastName.text.toString().trim(),
                    TextView.BufferType.EDITABLE
                )
                updateSignUpButton()
            }
        }


        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etPassword.text.toString().trim() == "") {
                    binding.tlPassword.error = getString(R.string.password_cant_be_empty_error)
                } else if (binding.etPassword.text.toString().trim().length !in 8..16) {
                    binding.tlPassword.error = getString(R.string.password_length_error)
                } else {
                    var hasNum = false
                    var hasLetter = false

                    for (i in binding.etPassword.text.toString().trim().indices) {
                        if (!hasNum) {
                            hasNum = binding.etPassword.text.toString().trim()[i] in '0'..'9'
                        }
                        if (!hasLetter) {
                            hasLetter = binding.etPassword.text.toString().trim()[i] in 'a'..'z' ||
                                    binding.etPassword.text.toString().trim()[i] in 'A'..'Z'
                        }
                        if (hasLetter && hasNum) break
                    }

                    if (hasLetter && hasNum) {
                        binding.tlPassword.error = null
                    } else {
                        binding.tlPassword.error = getString(R.string.passwords_contents_error)
                    }
                }

                if (binding.etPassword.text.toString()
                        .trim() != binding.etConfirmPassword.text.toString().trim()
                ) {
                    binding.tlConfirmPassword.error = getString(R.string.passwords_dont_match_error)
                } else {
                    binding.tlConfirmPassword.error = null
                }
                updateSignUpButton()
            }

        })

        binding.etPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.etPassword.setText(
                    binding.etPassword.text.toString().trim(),
                    TextView.BufferType.EDITABLE
                )
                updateSignUpButton()
            }
        }

        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etPassword.text.toString()
                        .trim() != binding.etConfirmPassword.text.toString().trim()
                ) {
                    binding.tlConfirmPassword.error = getString(R.string.passwords_dont_match_error)
                } else {
                    binding.tlConfirmPassword.error = null
                }
                updateSignUpButton()
            }

        })

        binding.etConfirmPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.etConfirmPassword.setText(
                    binding.etConfirmPassword.text.toString().trim(),
                    TextView.BufferType.EDITABLE
                )
                updateSignUpButton()
            }
        }

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

        val adapter = ArrayAdapter(this, R.layout.city_list_item, cities)
        binding.acCityMenu.setAdapter(adapter)

        binding.acCityMenu.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!cities.contains(binding.acCityMenu.text.toString())){
                    binding.tlCityMenu.error = "City is required"
                }
                else{
                    binding.tlCityMenu.error = null
                }
                updateSignUpButton()
            }

        })
    }

    fun updateSignUpButton(){
        binding.btnSignUp.isEnabled = (binding.etEmail.text.toString().trim() != "" && binding.tlEmail.error == null
                && binding.etFirstName.text.toString().trim()!= "" && binding.tlFirstName.error == null
                && binding.etLastName.text.toString().trim()!= "" && binding.tlLastName.error == null
                && binding.etPassword.text.toString().trim() != ""
                && binding.etPassword.text.toString().trim() == binding.etConfirmPassword.text.toString().trim()
                && binding.tlPassword.error == null && binding.tvEventCategoryError.visibility == View.INVISIBLE
                && cities.contains(binding.acCityMenu.text.toString()))

    }
    private fun updateEventCategoryStatus(){
        binding.tvEventCategoryError.visibility = if (!(binding.cbEducational.isChecked || binding.cbEntertainment.isChecked ||
                        binding.cbVolunteering.isChecked || binding.cbSports.isChecked)) View.VISIBLE else View.INVISIBLE
        updateSignUpButton()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}