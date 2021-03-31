package com.eventlocator.eventlocator.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.eventlocator.eventlocator.R
import com.eventlocator.eventlocator.data.Event
import com.eventlocator.eventlocator.data.eventfilter.*
import com.eventlocator.eventlocator.databinding.FragmentFilterUpcomingEventsByFollowedOrganizersBinding
import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class FilterUpcomingEventsByFollowedOrganizersFragment(var events: ArrayList<Event>): Fragment() {
    lateinit var binding:FragmentFilterUpcomingEventsByFollowedOrganizersBinding
    lateinit var activity: OnUpcomingEventsByFollowedOrganizersFiltered
    var startDate: LocalDate? = null
    var endDate: LocalDate? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFilterUpcomingEventsByFollowedOrganizersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                if ((binding.cgCity[i] as Chip).isChecked){
                    selectedDays.add(i)
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
                filter = EventNameFilter(binding.etEventName.text.toString().toString())
                result = filter.apply(result)
            }

            activity.getUpcomingEventsByFollowedOrganizers(result)

        }



        binding.cgType.setOnCheckedChangeListener { group, checkedId ->
            binding.cgCity.isEnabled = binding.cLocated.isChecked
        }

        binding.btnSelectDates.setOnClickListener {
            val builder: MaterialDatePicker.Builder<Pair<Long, Long>> = MaterialDatePicker.Builder.dateRangePicker()

            val calendarConstraints = CalendarConstraints.Builder()
            val endConstraint = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()
            calendarConstraints.setStart(endConstraint)
            calendarConstraints.setValidator(DateValidatorPointBackward.before(endConstraint))
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
        activity = context as OnUpcomingEventsByFollowedOrganizersFiltered
    }
}

interface OnUpcomingEventsByFollowedOrganizersFiltered{
    fun getUpcomingEventsByFollowedOrganizers(upcomingEvents: ArrayList<Event>)
}