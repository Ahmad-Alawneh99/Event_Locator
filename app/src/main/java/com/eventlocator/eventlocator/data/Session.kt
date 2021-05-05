package com.eventlocator.eventlocator.data

import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

data class Session(var id: Int, var date: String, var startTime: String, var endTime: String, var dayOfWeek: Int, var checkInTime: String): Serializable {

}