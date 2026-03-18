package com.example.myamover.route.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun formatIsoDateTime(
    isoString: String?,
    pattern: String
): String {
    if (isoString.isNullOrBlank()) return ""

    return try {
        val dateTime = LocalDateTime.parse(isoString)
        val formatter = DateTimeFormatter.ofPattern(pattern)
        dateTime.format(formatter)
    } catch (e: Exception) {
        ""
    }
}
