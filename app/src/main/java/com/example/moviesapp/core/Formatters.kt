package com.example.moviesapp.core

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Locale
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Double.oneDecimal(): String = String.format(Locale.US, "%.1f", this)

@RequiresApi(Build.VERSION_CODES.O)
fun String?.yyyyMMdd_to_ddMMMYYYY(): String {
    if (this.isNullOrBlank()) return "-"
    return try {
        val d = LocalDate.parse(this)
        d.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es")))
    } catch (_: Throwable) { this }
}