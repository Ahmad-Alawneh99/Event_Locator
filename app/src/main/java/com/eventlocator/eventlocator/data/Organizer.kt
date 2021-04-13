package com.eventlocator.eventlocator.data

class Organizer(var id: Long, var name: String, var email: String, var about: String,
                var rating: Double, var socialMediaAccounts: List<SocialMediaAccount>, var image: String, var numberOfFollowers: Int,
                var isFollowedByCurrentParticipant: Boolean) {
}