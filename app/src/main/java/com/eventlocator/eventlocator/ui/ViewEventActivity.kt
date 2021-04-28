package com.eventlocator.eventlocator.ui

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.data.Session
import com.eventlocator.eventlocator.databinding.ActivityViewEventBinding
import com.eventlocator.eventlocator.databinding.SessionDisplayBinding
import com.eventlocator.eventlocator.retrofit.EventService
import com.eventlocator.eventlocator.retrofit.RetrofitServiceFactory
import com.eventlocator.eventlocator.utilities.*
import com.google.android.gms.maps.model.LatLng
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.*

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
        binding.pbLoading.visibility = View.VISIBLE
        binding.btnAction.visibility = View.INVISIBLE
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        RetrofitServiceFactory.createServiceWithAuthentication(EventService::class.java, token!!)
                .getEventById(eventID).enqueue(object: Callback<Event>{
                    override fun onResponse(call: Call<Event>, response: Response<Event>) {
                        if (response.code()==202) {
                            event = response.body()!!
                            updateRegisterButton()
                            invalidateOptionsMenu()
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
                        binding.pbLoading.visibility = View.INVISIBLE
                    }

                    override fun onFailure(call: Call<Event>, t: Throwable) {
                        Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                "Error", "Can't connect to server", true)
                        binding.pbLoading.visibility = View.INVISIBLE
                    }

                })
    }

    fun loadEvent(){
        val imageBitmap = BitmapFactory.
        decodeStream(ByteArrayInputStream(Base64.decode(event.image, Base64.DEFAULT)))
        binding.ivEventImage.setImageBitmap(imageBitmap)
        binding.ivEventImage.setOnClickListener {
            val intent = Intent(this, ViewImageActivity::class.java)
            intent.putExtra("image", event.image)
            startActivity(intent)
        }
        val startDateFormatted = LocalDate.parse(event.startDate, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        val endDateFormatted = LocalDate.parse(event.endDate, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))

        binding.tvOrganizerName.text = event.organizerName

        binding.tvOrganizerName.setOnClickListener {
            val intent = Intent(this, OrganizerProfileActivity::class.java)
            intent.putExtra("organizerID", event.organizerID)
            startActivity(intent)
        }

        updateParticipantStatus()

        val layoutManager = object: LinearLayoutManager(this) {
            override fun canScrollVertically():Boolean =  false
        }
        binding.rvSessions.layoutManager = layoutManager
        val adapter = SessionDisplayAdapter(event.sessions)
        binding.rvSessions.adapter = adapter

        binding.tvEventName.text = event.name
        binding.tvEventDate.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                .format(startDateFormatted) + " - " + DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                .format(endDateFormatted)
        binding.tvRating.text = if(event.isFinished()) {
            if (event.rating == 0.0) "No ratings yet"
            else BigDecimal(event.rating).setScale(2).toString() + "/5"
        }
        else ""
        if (!event.isFinished()){
            binding.llRating.visibility = View.GONE
        }

        binding.tvEventStatus.text = event.getStatus()
        binding.tvDescription.text = event.description


        if (event.locatedEventData!=null) {
            binding.llCity.visibility = View.VISIBLE
            binding.llLocation.visibility = View.VISIBLE
            binding.tvCity.text = cities[event.locatedEventData!!.city]
            binding.tvLocation.setOnClickListener {
                val intent = Intent(this, ViewLocationActivity::class.java)
                val latLng = LatLng(event.locatedEventData!!.location[0], event.locatedEventData!!.location[1])
                intent.putExtra("latLng", latLng)
                startActivity(intent)
            }
        }
        else{
            binding.llCity.visibility = View.GONE
            binding.llLocation.visibility = View.GONE
        }


        val registrationCloseDateTime = LocalDateTime.parse(event.registrationCloseDateTime,
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))
        binding.tvRegistrationCloseDateTime.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DISPLAY)
                .format(registrationCloseDateTime)

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
            binding.llCancellationDate.visibility = View.VISIBLE
            binding.llCancellationReason.visibility = View.VISIBLE

            val cancellationDateTime = LocalDateTime.parse(event.canceledEventData!!.cancellationDateTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DEFAULT))

            binding.tvCancellationDateTime.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_TIME_DISPLAY)
                    .format(cancellationDateTime)
            binding.tvCancellationReason.text = event.canceledEventData!!.cancellationReason
        }
        else{
            binding.llCancellationDate.visibility = View.GONE
            binding.llCancellationReason.visibility = View.GONE
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!this::event.isInitialized)return false
        if (!event.isCanceled()) {
            if (event.isFinished() && event.hasParticipantAttended == ParticipantAttendanceStatus.TRUE.ordinal) {
                //TODO: Check if 48 hours passed and if participant already rated
                menu?.add(1, 1, 1, "Rate event")
            }
            if (event.isParticipantRegistered && !event.isFinished() && event.isLimitedLocated()) {
                menu?.add(1, 2, 2, "View QR codes")
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            1 -> {

            }

            2 -> {
                val intent = Intent(this, QRCodesActivity::class.java)
                intent.putExtra("eventID", eventID)
                intent.putExtra("participantID", getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                    .getLong(SharedPreferenceManager.instance.PARTICIPANT_ID_KEY,-1))
                intent.putExtra("sessions", event.sessions)
                startActivity(intent)
            }

            3 -> {

            }
        }



        return super.onOptionsItemSelected(item)
    }

    private fun register(){
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        val data = ArrayList<String>()
        data.add(MessagingService().getToken(this))
        RetrofitServiceFactory.createServiceWithAuthentication(EventService::class.java, token!!)
                .registerParticipantInEvent(event.id, data).enqueue(object: Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.code()==202){
                            event.isParticipantRegistered = true
                            updateRegisterButton()
                            invalidateOptionsMenu()
                            val startDate = LocalDate.from(DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT).parse(event.startDate))
                            val startTime = LocalTime.from(DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT).parse(event.sessions[0].startTime))
                            val startDateTime = startDate.atTime(startTime)
                            val message = "The event ${event.name} is starting in 12 hours, be sure to get ready"
                            NotificationUtils.scheduleNotification(this@ViewEventActivity, startDateTime, event.id.toInt(),message,event.id)
                        }
                        else if (response.code()==401){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity, "Error",
                                    "401: Unauthorized access",true)
                        }
                        else if (response.code() == 409){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                    "Error", "Event is full", false)
                            event.currentNumberOfParticipants = event.maxParticipants
                            updateRegisterButton()
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
                                "Error", t.message!!.toString(), true)
                    }

                })
    }

    private fun unregister(){
        val token = getSharedPreferences(SharedPreferenceManager.instance.SHARED_PREFERENCE_FILE, MODE_PRIVATE)
                .getString(SharedPreferenceManager.instance.TOKEN_KEY, "EMPTY")
        val data = ArrayList<String>()
        data.add(MessagingService().getToken(this))
        RetrofitServiceFactory.createServiceWithAuthentication(EventService::class.java, token!!)
                .unregisterParticipantInEvent(event.id, data).enqueue(object: Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.code()==202){
                            event.isParticipantRegistered = false
                            updateRegisterButton()
                            NotificationUtils.cancelNotification(this@ViewEventActivity, eventID)
                        }
                        else if (response.code()==401){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity, "Error",
                                    "401: Unauthorized access",true)
                        }
                        else if (response.code() == 409){
                            Utils.instance.displayInformationalDialog(this@ViewEventActivity,
                                    "Error", "You are already unregistered in the event", false)
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
                                "Error", t.message!!.toString(), true)
                    }

                })
    }

    private fun updateRegisterButton(){
        if (!event.isRegistrationClosed() && !event.isParticipantRegistered) {
            binding.btnAction.text = getString(R.string.register)
            binding.btnAction.setOnClickListener {
                val dialogAlert = Utils.instance.createSimpleDialog(this, "Register", "Are you sure that you want to register in this event?")
                dialogAlert.setPositiveButton("Yes"){di: DialogInterface, i: Int ->
                    register()
                }
                dialogAlert.setNegativeButton("No"){di: DialogInterface, i: Int ->}
                dialogAlert.create().show()

            }
            binding.btnAction.visibility = View.VISIBLE
        } else if (event.isParticipantRegistered) {
            val eventStartDate = LocalDate.parse(event.startDate,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
            val eventStartDateTime = eventStartDate.atTime(LocalTime.parse(event.sessions[0].startTime,
                    DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)))
            if (eventStartDateTime.minusHours(12).isAfter(LocalDateTime.now())) {
                binding.btnAction.text = getString(R.string.unregister)
                binding.btnAction.setOnClickListener {
                    val dialogAlert = Utils.instance.createSimpleDialog(this, "Unregister", "Are you sure that you want to unregister from this event?")
                    dialogAlert.setPositiveButton("Yes"){di: DialogInterface, i: Int ->
                        unregister()
                    }
                    dialogAlert.setNegativeButton("No"){di: DialogInterface, i: Int ->}
                    dialogAlert.create().show()
                }
                binding.btnAction.visibility = View.VISIBLE
            }
            else{
                binding.btnAction.visibility = View.INVISIBLE
            }
        }
        updateParticipantStatus()
    }

    private fun updateParticipantStatus(){
        if (event.getCurrentParticipantStatus() == ""){
            binding.tvParticipantStatus.visibility = View.GONE
        }
        else{
            binding.tvParticipantStatus.visibility = View.VISIBLE
            binding.tvParticipantStatus.text = event.getCurrentParticipantStatus()
        }
    }

}

class SessionDisplayAdapter(private val sessions: ArrayList<Session>):
        RecyclerView.Adapter<SessionDisplayAdapter.SessionDisplayViewHolder>(){

    inner class SessionDisplayViewHolder(var binding: SessionDisplayBinding):
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionDisplayViewHolder {
        val binding = SessionDisplayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionDisplayViewHolder(binding)
    }


    override fun onBindViewHolder(holder: SessionDisplayViewHolder, position: Int) {
        val date = LocalDate.parse(sessions[position].date, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DEFAULT))
        val timeFormatterDefault = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT)
        val timeFormatterDisplay = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DISPLAY)
        val startTime = LocalTime.parse(sessions[position].startTime, timeFormatterDefault)
        val endTime = LocalTime.parse(sessions[position].endTime, timeFormatterDefault)
        holder.binding.tvSessionDateAndDay.text = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()) +", "+
                DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY).format(date)

        holder.binding.tvStartTime.text = timeFormatterDisplay.format(startTime)
        holder.binding.tvEndTime.text = timeFormatterDisplay.format(endTime)

        if (sessions[position].checkInTime!=""){
            val checkInTime = timeFormatterDefault.parse(sessions[position].checkInTime)
            holder.binding.tvCheckInTime.text = timeFormatterDisplay.format(checkInTime)
        }
        else{
            holder.binding.llCheckInTime.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = sessions.size

}