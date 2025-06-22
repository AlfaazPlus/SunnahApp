package com.alfaazplus.sunnah.helpers

import androidx.room.TypeConverter
import java.util.Date

class UserDbConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
