package com.hasan.eventapp.utils.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.hasan.eventapp.R

/**
 * Shows an error Snackbar with customized styling
 * @param message The error message to display
 * @param duration How long to display the message
 */
fun View.showErrorSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(this, message, duration)
        .setBackgroundTint(resources.getColor(R.color.error_color, null))
        .setTextColor(resources.getColor(android.R.color.white, null))
        .show()
}

/**
 * Shows a success Snackbar with customized styling
 * @param message The success message to display
 * @param duration How long to display the message
 */
fun View.showSuccessSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(this, message, duration)
        .setBackgroundTint(resources.getColor(R.color.success_color, null))
        .setTextColor(resources.getColor(android.R.color.white, null))
        .show()
}

/**
 * Shows an info Snackbar with customized styling
 * @param message The info message to display
 * @param duration How long to display the message
 */
fun View.showInfoSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(this, message, duration)
        .setBackgroundTint(resources.getColor(R.color.info_color, null))
        .setTextColor(resources.getColor(android.R.color.white, null))
        .show()
}
