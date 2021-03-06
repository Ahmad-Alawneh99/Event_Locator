package com.eventlocator.eventlocator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.databinding.ActivitySignUpBinding
import com.eventlocator.eventlocator.utilities.Utils

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUp.isEnabled = false
        binding.btnSignUp.setOnClickListener {
            //TODO: handle next step
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
    }

    fun updateSignUpButton(){
        binding.btnSignUp.isEnabled = (binding.etEmail.text.toString().trim() != "" && binding.tlEmail.error == null
                && binding.etFirstName.text.toString().trim()!= "" && binding.tlFirstName.error == null
                && binding.etLastName.text.toString().trim()!= "" && binding.tlLastName.error == null
                && binding.etPassword.text.toString().trim() != ""
                && binding.etPassword.text.toString().trim() == binding.etConfirmPassword.text.toString().trim()
                && binding.tlPassword.error == null)

    }
}