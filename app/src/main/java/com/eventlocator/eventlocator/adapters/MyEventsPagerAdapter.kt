package com.eventlocator.eventlocator.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eventlocator.eventlocator.ui.MyCanceledEventsFragment
import com.eventlocator.eventlocator.ui.MyPreviousEventsFragment
import com.eventlocator.eventlocator.ui.MyUpcomingEventsFragment
import com.eventlocator.eventlocator.ui.UpcomingEventsFragment

class MyEventsPagerAdapter(fa: FragmentActivity, var numberOfTabs: Int): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return numberOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return MyUpcomingEventsFragment()
            1 -> return MyPreviousEventsFragment()
            2 -> return MyCanceledEventsFragment()
        }
        return MyUpcomingEventsFragment()
    }
}