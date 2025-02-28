package com.hasan.eventapp.utils.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Formats a timestamp (in milliseconds) to a date string using the specified format
 * @param pattern The date format pattern
 * @param locale The locale to use for formatting (defaults to system locale)
 * @return Formatted date string
 */
fun Long.formatToDate(
    pattern: String = "dd MMM yyyy, HH:mm",
    locale: Locale = Locale.getDefault()
): String {
    val dateFormat = SimpleDateFormat(pattern, locale)
    return dateFormat.format(Date(this))
}

/**
 * Converts a timestamp (in milliseconds) to a Date object
 * @return Date object
 */
fun Long.toDate(): Date = Date(this)