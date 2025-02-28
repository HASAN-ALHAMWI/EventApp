package com.hasan.eventapp.utils

class InputValidationException private constructor(
    override val message: String,
    private val fieldErrors: Map<String, String>
) : Exception(message) {

    constructor(fieldErrors: Map<String, String>) : this(
        fieldErrors.values.firstOrNull() ?: "Validation failed",
        fieldErrors
    )

    fun getError(field: String): String? = fieldErrors[field]
}