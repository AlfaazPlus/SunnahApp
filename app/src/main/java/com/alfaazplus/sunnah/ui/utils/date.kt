package com.alfaazplus.sunnah.ui.utils

import android.text.format.DateFormat
import java.util.Date

fun formatDateTimeShort(date: Date): String { // Format the date to a short date and time string
    return DateFormat
        .format("dd MMM, yyyy hh:mm a", date)
        .toString()
}