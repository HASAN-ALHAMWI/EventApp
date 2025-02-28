package com.hasan.eventapp.utils.extensions

import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Sets or clears error on TextInputLayout with proper error state handling
 * @param errorText The error text to show or null to clear error
 */
fun TextInputLayout.setErrorWithVisibility(errorText: String?) {
    isErrorEnabled = errorText != null
    error = errorText
}

fun TextInputEditText.formatCardNumber() {
    doAfterTextChanged { text ->
        if (!text.isNullOrEmpty()) {
            val formatted = text.toString().replace(" ", "").chunked(4).joinToString(" ")

            if (formatted != text.toString()) {
                setText(formatted)
                setSelection(formatted.length)
            }
        }
    }
}

fun TextInputEditText.formatExpiryDate() {
    doAfterTextChanged { text ->
        if (!text.isNullOrEmpty()) {
            val input = text.toString().replace("/", "")

            if (input.length > 2) {
                val month = input.substring(0, 2)
                val year = input.substring(2, minOf(4, input.length))
                val formatted = "$month/$year"

                if (formatted != text.toString()) {
                    setText(formatted)
                    setSelection(formatted.length)
                }
            }
        }
    }
}