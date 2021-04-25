package com.eventlocator.eventlocator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eventlocator.eventlocator.databinding.ActivityRateEventBinding

class RateEventActivity : AppCompatActivity() {
    lateinit var binding: ActivityRateEventBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}