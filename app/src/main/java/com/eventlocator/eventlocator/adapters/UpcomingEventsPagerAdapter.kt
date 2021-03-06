package com.eventlocator.eventlocator.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eventlocator.eventlocator.ui.UpcomingEventsByFollowedOrganizersFragment
import com.eventlocator.eventlocator.ui.UpcomingEventsFragment

class UpcomingEventsPagerAdapter( fa: FragmentActivity, var numberOfTabs: Int): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return numberOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return UpcomingEventsFragment()
            1 -> return UpcomingEventsByFollowedOrganizersFragment()
        }
        return UpcomingEventsFragment()
    }
}