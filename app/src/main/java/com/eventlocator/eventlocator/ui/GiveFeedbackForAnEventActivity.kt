package com.eventlocator.eventlocator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eventlocator.eventlocator.databinding.ActivityGiveFeedbackForAnEventBinding

class GiveFeedbackForAnEventActivity : AppCompatActivity() {
    lateinit var binding: ActivityGiveFeedbackForAnEventBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiveFeedbackForAnEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}