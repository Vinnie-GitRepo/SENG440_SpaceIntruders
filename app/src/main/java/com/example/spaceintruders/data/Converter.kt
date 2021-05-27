package com.example.spaceintruders.data

import androidx.room.TypeConverter
import java.util.*


class Converter {
    @TypeConverter
    fun fromTimestamp(value: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = Date(value)
        return calendar
    }


    @TypeConverter
    fun dateToTimestamp(date: Calendar): Long {
        return date.time.time
    }
}