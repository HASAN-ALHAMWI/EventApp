package com.hasan.eventapp.utils.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

/**
 * Hides the soft keyboard from the current fragment
 */
fun Fragment.hideKeyboard() {
    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}

/**
 * Shows a customizable alert dialog with configurable options
 *
 * @param title Dialog title
 * @param message Dialog message
 * @param positiveButtonText Text for positive button (defaults to "OK")
 * @param negativeButtonText Text for negative button (defaults to "Cancel")
 * @param onPositiveClick Callback for positive button click
 * @param onNegativeClick Callback for negative button click (optional)
 * @param cancelable Whether dialog can be dismissed by clicking outside (defaults to true)
 */
fun Fragment.showDialog(
    title: String,
    message: String,
    positiveButtonText: String = "OK",
    negativeButtonText: String? = "Cancel",
    onPositiveClick: () -> Unit,
    onNegativeClick: (() -> Unit)? = null,
    cancelable: Boolean = true
) {
    val builder = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonText) { _, _ -> onPositiveClick() }
        .setCancelable(cancelable)

    // Add negative button only if text is provided
    negativeButtonText?.let {
        builder.setNegativeButton(it) { _, _ -> onNegativeClick?.invoke() }
    }

    builder.show()
}