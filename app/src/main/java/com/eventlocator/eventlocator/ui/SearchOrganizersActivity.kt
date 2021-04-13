package com.eventlocator.eventlocator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eventlocator.eventlocator.databinding.ActivitySearchOrganizersBinding

class SearchOrganizersActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchOrganizersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchOrganizersBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}