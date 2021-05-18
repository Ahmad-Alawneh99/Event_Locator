package com.eventlocator.eventlocator.data

import android.view.View
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import com.eventlocator.eventlocator.utilities.ParticipantAttendanceStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Event(var id: Long, var name: String, var description: String, var categories: ArrayList<Int>,
            var startDate: String, var endDate: String, var registrationCloseDateTime: String,
            var rating: Double, var sessions: ArrayList<Session>, var feedback: Feedback?, var locatedEventData: LocatedEventData?,
            var canceledEventData: CanceledEventData?, var image: String, var organizerID: Long, var organizerName: String,
            var maxParticipants: Int, var isParticipantRegistered: Boolean, var hasParticipantAttended: Int, var currentNumberOfParticipants: Int) {

    fun getStatus(): String{
        return if (isCanceled()){
            "This event is canceled"
        }
        else if (isFinished()){
            "This event has finished"
        }
        else if (!isRegistrationClosed()){
            if (isFull()){
                return "This event is full"
            }
            else{
                return "Registration ongoing"
            }
        }
        else if (isRegistrationClosed()){
            "Registration closed"
        }
        else if (getCurrentSession()!=null){
            "Session #"+getCurrentSession()!!.id+" is happening now"
        }
        else{
            "This event is active"
        }
    }

    fun isCanceled(): Boolean = canceledEventData != null

    fun isRegistrationClosed(): Boolean {
        val registrationCloseDateTime =
                LocalDateTime.parse(registrationCloseDateTime,
                        DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))
        return LocalDateTime.now().isAfter(registrationCloseDateTime)
    }

    fun isFinished(): Boolean {
        val eventEndDate = LocalDate.parse(endDate, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        val eventEndDateTime = eventEndDate.atTime(LocalTime.parse(sessions[sessions.size-1].endTime,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))

        return LocalDateTime.now().isAfter(eventEndDateTime)
    }

    fun isLimitedLocated():Boolean{
        return maxParticipants!=-1 && locatedEventData!=null
    }

    fun getCurrentSession(): Session? {
        for(j in 0 until sessions.size) {
            val sessionDate = LocalDate.parse(sessions[j].date,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
            val sessionStartDateTime = sessionDate.atTime(LocalTime.parse(sessions[j].startTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            val sessionEndDateTime = sessionDate.atTime(LocalTime.parse(sessions[j].endTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            if (LocalDateTime.now().isAfter(sessionStartDateTime) && LocalDateTime.now().isBefore(sessionEndDateTime)) {
                return sessions[j]
            }
        }
        return null
    }

    fun isFull(): Boolean{
        return this.maxParticipants!=-1 && this.maxParticipants == this.currentNumberOfParticipants
    }

    fun getCurrentParticipantStatus(): String{
        return if (this.hasParticipantAttended == ParticipantAttendanceStatus.TRUE.ordinal){
            if (this.feedback!=null){
                "You left feedback for the event"
            }
            else  "You attended this event"
        }
        else if (this.isParticipantRegistered){
            if (isCanceled()) "You were registered in this event"
            else "You are registered in this event"
        }
        else{
            ""
        }
    }
}