package com.hasan.eventapp.utils.extensions

import com.hasan.eventapp.utils.Constants
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

// Email validation
fun String.isValidEmail(): Boolean =
    Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+").matcher(this).matches()

// Password validation
fun String.isValidPassword(): Boolean =
    length in Constants.MIN_PASSWORD_LENGTH..Constants.MAX_PASSWORD_LENGTH

// Card number validation using Luhn algorithm
fun String.isValidCardNumber(): Boolean {
    // Remove any spaces or non-digit characters
    val cleanNumber = replace(" ", "")

    return cleanNumber.length == 16
}

// CVV validation
fun String.isValidCvv(): Boolean {
    // CVV should be 3 or 4 digits
    return matches(Regex("^[0-9]{3,4}$"))
}

// Expiry date validation
fun String.isValidExpiryDate(): Boolean {
    // Check format (MM/YY)
    if (!matches(Regex("^(0[1-9]|1[0-2])/[0-9]{2}$"))) {
        return false
    }

    return try {
        val formatter = DateTimeFormatter.ofPattern("MM/yy")
        val expiryMonth = YearMonth.parse(this, formatter)

        // Check if the expiry date is in the future
        val currentMonth = YearMonth.now()
        expiryMonth > currentMonth
    } catch (e: DateTimeParseException) {
        false
    }
}

// Helper extension to format card number with spaces
fun String.formatCardNumber(): String {
    return replace(" ", "")
}

// Helper extension to format expiry date
fun String.formatExpiryDate(): String {
    return when {
        length > 2 -> {
            val month = substring(0, 2)
            val year = substring(2)
            "$month/$year"
        }
        else -> this
    }
}