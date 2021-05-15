package com.eventlocator.eventlocator.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.databinding.ActivityWelcomeBinding
import com.eventlocator.eventlocator.utilities.SharedPreferenceManager

class WelcomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)

        if (sharedPreferences.contains(SharedPreferenceManager.instance.FIRST_TIME_KEY)){
            startActivity(Intent(this, EventsActivity::class.java))
        }

        val editor = sharedPreferences.edit()
        editor.putBoolean(SharedPreferenceManager.instance.FIRST_TIME_KEY, false)
        editor.apply()

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}