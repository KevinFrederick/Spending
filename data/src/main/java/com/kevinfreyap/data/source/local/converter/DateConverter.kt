package com.kevinfreyap.data.source.local.converter

import androidx.room.TypeConverter
import java.time.Instant

class DateConverter {
    @TypeConverter
    fun fromInstant(instant: Instant): Long {
        return instant.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(millis: Long): Instant {
        return Instant.ofEpochMilli(millis)
    }
}