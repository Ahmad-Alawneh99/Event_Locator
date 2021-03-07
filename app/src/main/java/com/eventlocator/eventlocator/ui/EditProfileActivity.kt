package com.eventlocator.eventlocator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eventlocator.eventlocator.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
    }
}