package com.eventlocator.eventlocator.data

class Event(var id: Long, var name: String, var description: String, var categories: List<Int>,
            var startDate: String, var endDate: String, var registrationCloseDateTime: String,
            var rating: Double, var sessions: List<Session>, var feedback: Feedback?, var locatedEventData: LocatedEventData?,
            var canceledEventData: CanceledEventData?, var image: String, var organizerID: Long, var organizerName: String,
            var maxParticipants: Int, var isParticipantRegistered: Boolean, var hasParticipantAttended: Int) {
}