package com.eventlocator.eventlocator.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.data.Session
import com.eventlocator.eventlocator.databinding.ActivityViewEventBinding
import com.eventlocator.eventlocator.retrofit.EventService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.*
import okhttp3.ResponseBody
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
                        if (response.code()==202) {
                            event = response.body()!!
                            updateRegisterButton()
                            loadEvent()
                        }
                        else if (response.code()==401){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity, "Error",
                                    "401: Unauthorized access",true)
                        }
                        else if(response.code()==404){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                    "Error", "Event not found",true)
                        }
                        else if (response.code() == 500){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                    "Error", "Server issue, please try again later", true)
                        }
                    }

                    override fun onFailure(call: Call<Event>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                "Error", "Can't connect to server", true)
                    }

                })
    }

    fun loadEvent(){
        //TODO: add sessions
        binding.ivEventImage.setImageBitmap(BitmapFactory.
        decodeStream(ByteArrayInputStream(Base64.decode(event.image, Base64.DEFAULT))))
        val startDateFormatted = LocalDate.parse(event.startDate, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        val endDateFormatted = LocalDate.parse(event.endDate, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))

        binding.tvOrganizerName.text = event.organizerName

        binding.tvOrganizerName.setOnClickListener {
            val intent = Intent(this, OrganizerProfileActivity::class.java)
            intent.putExtra("organizerID", event.organizerID)
            startActivity(intent)
        }

        binding.tvEventName.text = event.name
        binding.tvEventDate.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                .format(startDateFormatted) + " - " + DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                .format(endDateFormatted)
        binding.tvEventRating.text = if(event.isFinished())event.rating.toString()
        else "This event didn't finish yet"
        binding.tvEventStatus.text = event.getStatus()
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

        if (event.isCanceled()) {
            val cancellationDateTime = LocalDateTime.parse(event.canceledEventData!!.cancellationDateTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))

            binding.tvCancellationDateTime.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DISPLAY)
                    .format(cancellationDateTime)
            binding.tvCancellationReason.text = event.canceledEventData!!.cancellationReason
        }

    }

    private fun register(){
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")

        RetrofitServiceFactory.createServiceWithAuthentication(EventService::class.java, token!!)
                .registerParticipantInEvent(event.id).enqueue(object: Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.code()==202){
                            event.isParticipantRegistered = true
                            updateRegisterButton()
                        }
                        else if (response.code()==401){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity, "Error",
                                    "401: Unauthorized access",true)
                        }
                        else if (response.code() == 409){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                    "Error", "You are already registered in the event", true)
                        }
                        else if (response.code() == 406){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                    "Error", "event/participant not found", true)
                        }
                        else if (response.code() == 500){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                    "Error", "Server issue, please try again later", true)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                "Error", "Can't connect to server", true)
                    }

                })
    }

    private fun unregister(){
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")

        RetrofitServiceFactory.createServiceWithAuthentication(EventService::class.java, token!!)
                .unregisterParticipantInEvent(event.id).enqueue(object: Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.code()==202){
                            event.isParticipantRegistered = false
                            updateRegisterButton()
                        }
                        else if (response.code()==401){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity, "Error",
                                    "401: Unauthorized access",true)
                        }
                        else if (response.code() == 409){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                    "Error", "You are already unregistered in the event", true)
                        }
                        else if (response.code() == 406){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                    "Error", "event/participant not found", true)
                        }
                        else if (response.code() == 500){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                    "Error", "Server issue, please try again later", true)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                "Error", "Can't connect to server", true)
                    }

                })
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

    private fun updateRegisterButton(){
        if (!event.isRegistrationClosed() && !event.isParticipantRegistered) {
            binding.btnAction.text = getString(R.string.register)
            binding.btnAction.setOnClickListener { register() }
        } else if (event.isParticipantRegistered) {
            val eventStartDate = LocalDate.parse(event.startDate,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
            val eventStartDateTime = eventStartDate.atTime(LocalTime.parse(event.sessions[0].startTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            if (eventStartDateTime.minusHours(12).isAfter(LocalDateTime.now())) {
                binding.btnAction.text = getString(R.string.unregister)
                binding.btnAction.setOnClickListener { unregister() }
            }
            else{
                binding.btnAction.visibility = View.GONE
            }
        }
    }

}