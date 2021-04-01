package com.eventlocator.eventlocator.ui

import android.graphics.BitmapFactory
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.data.Session
import com.eventlocator.eventlocator.databinding.ActivityViewEventBinding
import com.eventlocator.eventlocator.retrofit.EventService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ViewEventActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewEventBinding
    lateinit var event: Event
    var eventID: Long = -1
    lateinit var cities: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cities = listOf(getString(R.string.Amman),getString(R.string.Zarqa),getString(R.string.Balqa)
                ,getString(R.string.Madaba),getString(R.string.Irbid),getString(R.string.Mafraq)
                ,getString(R.string.Jerash),getString(R.string.Ajloun),getString(R.string.Karak)
                ,getString(R.string.Aqaba),getString(R.string.Maan),getString(R.string.Tafila))
        eventID = intent.getLongExtra("eventID", -1)
        getAndLoadEvent()

    }

    override fun onResume() {
        super.onResume()
        getAndLoadEvent()
    }

    private fun getAndLoadEvent(){
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        RetrofitServiceFactory.createServiceWithAuthentication(EventService::class.java, token!!)
                .getEventById(eventID).enqueue(object: Callback<Event>{
                    override fun onResponse(call: Call<Event>, response: Response<Event>) {
                        //TODO: Check request codes
                        event = response.body()!!
                        loadEvent()
                        if (!isRegistrationClosed() && !event.isParticipantRegistered){
                            binding.btnAction.text = getString(R.string.register)
                            //TODO: set click listener for registration
                        }
                        else if (event.isParticipantRegistered){
                            val eventStartDate = LocalDate.parse(event.startDate,
                                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
                            val eventStartDateTime = eventStartDate.atTime(LocalTime.parse(event.sessions[0].startTime,
                                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
                            if (eventStartDateTime.minusHours(12).isAfter(LocalDateTime.now())){
                                binding.btnAction.text = getString(R.string.register)
                                //TODO: set click listener to cancel registration
                            }
                        }
                    }

                    override fun onFailure(call: Call<Event>, t: Throwable) {
                        //TODO: Handle failure
                    }

                })
    }

    fun loadEvent(){
        binding.ivEventImage.setImageBitmap(BitmapFactory.
        decodeStream(ByteArrayInputStream(Base64.decode(event.image, Base64.DEFAULT))))
        val startDateFormatted = LocalDate.parse(event.startDate, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        val endDateFormatted = LocalDate.parse(event.endDate, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))

        //TODO: set click listener to open organizer profile
        binding.tvOrganizerName.text = event.organizerName

        binding.tvEventName.text = event.name
        binding.tvEventDate.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                .format(startDateFormatted) + " - " + DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                .format(endDateFormatted)
        binding.tvEventRating.text = if(isFinished())event.rating.toString()
        else "This event didn't finish yet"
        binding.tvEventStatus.text = getEventStatus()
        binding.tvDescription.text = event.description

        //TODO: surround optional data with card views and hide them when there is no data

        if (event.locatedEventData!=null) {
            binding.tvCity.text = cities[event.locatedEventData!!.city]
            val location = Geocoder(this).getFromLocation(
                    event.locatedEventData!!.location[0],
                    event.locatedEventData!!.location[1], 1)

            binding.tvLocation.text = if (location.size == 0) "Unnamed location" else location[0].getAddressLine(0)
            //TODO: Set click listener to show location on map
        }


        val registrationCloseDateTime = LocalDateTime.parse(event.registrationCloseDateTime,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))
        binding.tvRegistrationCloseDateTime.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DISPLAY)
                .format(registrationCloseDateTime)

        //TODO: find a better way to do this
        var categories = ""
        for(i in 0 until event.categories.size){
            categories+= when(event.categories[i]){
                EventCategory.EDUCATIONAL.ordinal -> getString(R.string.educational)
                EventCategory.ENTERTAINMENT.ordinal -> getString(R.string.entertainment)
                EventCategory.VOLUNTEERING.ordinal -> getString(R.string.volunteering)
                EventCategory.SPORTS.ordinal -> getString(R.string.sports)
                else -> ""
            }
            if (i!=event.categories.size-1)categories+=','
        }

        binding.tvCategories.text = categories

        if (event.canceledEventData!=null) {
            val cancellationDateTime = LocalDateTime.parse(event.canceledEventData!!.cancellationDateTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))

            binding.tvCancellationDateTime.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DISPLAY)
                    .format(cancellationDateTime)
            binding.tvCancellationReason.text = event.canceledEventData!!.cancellationReason
        }

    }

    private fun getEventStatus(): String{
        //TODO: Change to fit participant
        return if (isCanceled()){
            "This event is canceled"
        }
        else if (isFinished()){
            "This event has finished"
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

    private fun getParticipantStatus(): String{
        if (event.hasParticipantAttended == ParticipantAttendanceStatus.TRUE.ordinal){
            return "You attended this event"
        }
        else if (event.isParticipantRegistered){
            return "You are registered in this event"
        }
        else{
            binding.tvParticipantStatus.visibility = View.GONE
            return ""
        }
    }


    private fun isCanceled(): Boolean = event.canceledEventData != null


    private fun isRegistrationClosed(): Boolean {
        val registrationCloseDateTime =
                LocalDateTime.parse(event.registrationCloseDateTime,
                        DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))
        return LocalDateTime.now().isAfter(registrationCloseDateTime)
    }

    private fun isFinished(): Boolean {
        val eventEndDate = LocalDate.parse(event.endDate, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        val eventEndDateTime = eventEndDate.atTime(LocalTime.parse(event.sessions[event.sessions.size-1].endTime,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))

        return LocalDateTime.now().isAfter(eventEndDateTime)
    }

    private fun hasStarted(): Boolean{
        val eventStartDate = LocalDate.parse(event.startDate,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        val eventStartDateTime = eventStartDate.atTime(LocalTime.parse(event.sessions[0].startTime,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
        return LocalDateTime.now().isAfter(eventStartDateTime)
    }

    private fun isLimitedLocated():Boolean{
        return event.maxParticipants!=-1 && event.locatedEventData!=null
    }

    private fun getCurrentSession(): Session? {
        for(j in 0 until event.sessions.size) {
            val sessionDate = LocalDate.parse(event.sessions[j].date,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
            val sessionStartDateTime = sessionDate.atTime(LocalTime.parse(event.sessions[j].startTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            val sessionEndDateTime = sessionDate.atTime(LocalTime.parse(event.sessions[j].endTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            if (LocalDateTime.now().isAfter(sessionStartDateTime) && LocalDateTime.now().isBefore(sessionEndDateTime)) {
                return event.sessions[j]
            }
        }
        return null
    }

    private fun getCurrentLimitedSessionIncludingCheckInTime(): Session?{
        //never use before checking if the event was limited located
        for(j in 0 until event.sessions.size) {
            val sessionDate = LocalDate.parse(event.sessions[j].date,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
            val sessionCheckInDateTime = sessionDate.atTime(LocalTime.parse(event.sessions[j].checkInTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            val sessionEndDateTime = sessionDate.atTime(LocalTime.parse(event.sessions[j].endTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            if (LocalDateTime.now().isAfter(sessionCheckInDateTime) && LocalDateTime.now().isBefore(sessionEndDateTime)) {
                return event.sessions[j]
            }
        }
        return null
    }
}