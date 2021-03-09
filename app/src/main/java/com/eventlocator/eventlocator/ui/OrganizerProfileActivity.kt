package com.eventlocator.eventlocator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eventlocator.eventlocator.databinding.ActivityOrganizerProfileBinding

class OrganizerProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrganizerProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}