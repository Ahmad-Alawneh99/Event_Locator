package com.eventlocator.eventlocator.data

import com.eventlocator.eventlocator.utilities.DateTimeFormat
import com.eventlocator.eventlocator.utilities.DateTimeFormatterFactory
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

data class Session(var id: Int, var date: String, var startTime: String, var endTime: String, var dayOfWeek: Int, var checkInTime: String): Serializable {
    //TODO: Modify to support arabic
    override fun toString(): String {
        val date = LocalDate.parse(date, DateTimeFormatterFactory.createDateTimeFormatter(
            DateTimeFormat.DATE_DEFAULT))
        val startTime = LocalTime.parse(startTime, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT))
        val endTime = LocalTime.parse(endTime, DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT))
        val checkInTime = if (checkInTime!="") LocalTime.parse(checkInTime,
            DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DEFAULT))
        else null
        val timeFormatter = DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.TIME_DISPLAY)
        return "Date: " + DateTimeFormatterFactory.createDateTimeFormatter(DateTimeFormat.DATE_DISPLAY).format(date) + "\n" +
                "Start time: "+ timeFormatter.format(startTime) +
                "\nEnd time: " + timeFormatter.format(endTime) +
                if (checkInTime!=null) "\nCheck-in time: " + timeFormatter.format(checkInTime) else ""

    }
}