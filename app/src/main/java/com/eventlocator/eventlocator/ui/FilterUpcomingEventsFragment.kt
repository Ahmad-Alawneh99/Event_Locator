package com.eventlocator.eventlocator.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.Pair
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.data.Participant
import com.eventlocator.eventlocator.data.eventfilter.*
import com.eventlocator.eventlocator.databinding.FragmentFilterUpcomingEventsBinding
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class FilterUpcomingEventsFragment(var events: ArrayList<Event>): Fragment() {
    lateinit var binding:FragmentFilterUpcomingEventsBinding
    lateinit var activity: OnUpcomingEventsFiltered
    var startDate: LocalDate? = null
    var endDate: LocalDate? = null
    lateinit var participant: Participant
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFilterUpcomingEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        participant = (getActivity() as EventsActivity).participant

        for(i in 0 until binding.cgCategory.size){
            (binding.cgCategory[i] as Chip).isChecked = i in participant.preferredEventCategories
        }

        for(i in 0 until binding.cgCity.size){
            (binding.cgCity[i] as Chip).isChecked = i == participant.city
        }

        binding.btnApply.setOnClickListener {
            val selectedTypes = ArrayList<Int>()
            val selectedCategories = ArrayList<Int>()
            val selectedCities = ArrayList<Int>()
            val selectedDays = ArrayList<Int>()

            for(i in 0 until binding.cgType.size){
                if ((binding.cgType[i] as Chip).isChecked){
                    selectedTypes.add(i)
                }
            }


            for(i in 0 until binding.cgCategory.size){
                if ((binding.cgCategory[i] as Chip).isChecked){
                    selectedCategories.add(i)
                }
            }

            for(i in 0 until binding.cgCity.size){
                if ((binding.cgCity[i] as Chip).isChecked){
                    selectedCities.add(i)
                }
            }

            for(i in 0 until binding.cgDayOfWeek.size){
                if ((binding.cgDayOfWeek[i] as Chip).isChecked){
                    if (i==0)
                        selectedDays.add(6)
                    else
                        selectedDays.add(i-1)
                }
            }

            var filter: Filter = TypeFilter(selectedTypes)
            var result = filter.apply(events)
            filter = CategoryFilter(selectedCategories)
            result = filter.apply(result)
            filter = CityFilter(selectedCities)
            result = filter.apply(result)
            filter = DayOfWeekFilter(selectedDays)
            result = filter.apply(result)
            if (startDate!=null){
                filter = DatePeriodFilter(startDate!!, endDate!!)
                result = filter.apply(result)
            }

            if (binding.etEventName.text.toString().trim()!=""){
                filter = EventNameFilter(binding.etEventName.text.toString().trim())
                result = filter.apply(result)
            }

            if (binding.etOrganizerName.text.toString().trim()!=""){
                filter = OrganizerNameFilter(binding.etOrganizerName.text.toString().trim())
                result = filter.apply(result)
            }

            filter = RegistrationClosedFilter(binding.cbShowRegistrationClosedEvents.isChecked)
            result = filter.apply(result)
            filter = FullFilter(binding.cbShowFullEvents.isChecked)
            result = filter.apply(result)

            result = result.filter { event ->
                event.canceledEventData==null
            } as ArrayList<Event>


            activity.getUpcomingEvents(result)

        }

        binding.cgType.setOnCheckedChangeListener { group, checkedId ->
            binding.cgCity.isEnabled = binding.cLocated.isChecked
        }

        binding.btnSelectDates.setOnClickListener {
            val builder: MaterialDatePicker.Builder<Pair<Long, Long>> = MaterialDatePicker.Builder.dateRangePicker()

            val calendarConstraints = CalendarConstraints.Builder()
            val startConstraint = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()
            calendarConstraints.setStart(startConstraint)
            calendarConstraints.setValidator(DateValidatorPointForward.from(startConstraint))
            builder.setCalendarConstraints(calendarConstraints.build())
            builder.setTitleText("Select start and end dates")
            val picker = builder.build()
            picker.addOnPositiveButtonClickListener {
                val from: LocalDate = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(it.first!!),
                        ZoneId.systemDefault()).toLocalDate()
                val to: LocalDate = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(it.second!!),
                        ZoneId.systemDefault()).toLocalDate()

                startDate = from
                endDate = to

                binding.tvStartDate.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                        .format(startDate)

                binding.tvEndDate.text = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY)
                        .format(endDate)

            }
            picker.show(parentFragmentManager, builder.build().toString())
        }

        binding.btnRemoveDates.setOnClickListener {
            startDate = null
            endDate = null
            binding.tvStartDate.text = getString(R.string.select_date)
            binding.tvEndDate.text = getString(R.string.select_date)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as OnUpcomingEventsFiltered
    }
}

interface OnUpcomingEventsFiltered{
    fun getUpcomingEvents(upcomingEvents: ArrayList<Event>)
}