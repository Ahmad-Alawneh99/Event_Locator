package com.eventlocator.eventlocator.data

class Organizer(var id: Long, var name: String, var email: String, var about: String,
                var rating: Double, var socialMediaAccounts: List<SocialMediaAccount>,
                var upcomingEvents: List<Event>, var previousEvents: List<Event>,
                var canceledEvents: List<Event>, var image: String, var numberOfFollowers: Int,
                var isFollowedByCurrentParticipant: Boolean) {
}