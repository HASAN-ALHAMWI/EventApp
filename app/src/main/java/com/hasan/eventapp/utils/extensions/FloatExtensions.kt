package com.hasan.eventapp.utils.extensions

import java.util.Locale

/**
 * Formats a float as a price string with currency symbol
 * @return Formatted price string (e.g., "$10.99")
 */
fun Float.formatPrice(): String {
    return String.format(Locale.US, "$%.2f", this)
}