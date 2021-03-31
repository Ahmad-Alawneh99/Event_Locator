package com.eventlocator.eventlocator.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eventlocator.eventlocator.ui.UpcomingEventsByFollowedOrganizersFragment
import com.eventlocator.eventlocator.ui.UpcomingEventsFragment

class UpcomingEventsPagerAdapter(fa: FragmentActivity, var numberOfTabs: Int): FragmentStateAdapter(fa) {
    lateinit var upcomingEventsFragment: UpcomingEventsFragment
    lateinit var upcomingEventsByFollowedOrganizersFragment: UpcomingEventsByFollowedOrganizersFragment
    override fun getItemCount(): Int {
        return numberOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> {
                upcomingEventsFragment = UpcomingEventsFragment()
                return upcomingEventsFragment
            }
            1 -> {
                upcomingEventsByFollowedOrganizersFragment = UpcomingEventsByFollowedOrganizersFragment()
                return upcomingEventsByFollowedOrganizersFragment
            }
        }
        return UpcomingEventsFragment()
    }
}