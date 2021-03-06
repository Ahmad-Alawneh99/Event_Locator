package com.eventlocator.eventlocator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.adapters.UpcomingEventsPagerAdapter
import com.eventlocator.eventlocator.databinding.ActivityEventsBinding
import com.google.android.material.tabs.TabLayoutMediator

class EventsActivity : AppCompatActivity() {
    lateinit var binding: ActivityEventsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val pagerAdapter = UpcomingEventsPagerAdapter(this, 2)
        binding.pagerEvents.adapter = pagerAdapter

        TabLayoutMediator(binding.tlEvents, binding.pagerEvents){ tab, position ->
            when (position){
                0 -> tab.text = getString(R.string.upcoming_events)
                1 -> tab.text = "Temp"
            }

        }.attach()

        binding.pagerEvents.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Toast.makeText(applicationContext, "text", Toast.LENGTH_SHORT).show()
            }

        })

    }
}