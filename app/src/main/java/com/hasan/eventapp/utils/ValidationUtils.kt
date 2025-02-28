package com.hasan.eventapp.utils

import com.hasan.eventapp.utils.extensions.isValidCardNumber
import com.hasan.eventapp.utils.extensions.isValidCvv
import com.hasan.eventapp.utils.extensions.isValidEmail
import com.hasan.eventapp.utils.extensions.isValidExpiryDate
import com.hasan.eventapp.utils.extensions.isValidPassword

object ValidationUtils {

    fun validateLoginInput(email: String, password: String): ValidationResult {
        val errors = mutableMapOf<String, String>()

        when {
            email.isEmpty() -> errors["email"] = "Email cannot be empty"
            !email.isValidEmail() -> errors["email"] = "Invalid email format"
        }

        when {
            password.isEmpty() -> errors["password"] = "Password cannot be empty"
            !password.isValidPassword() -> errors["password"] =
                "Password must be between 6-20 characters"
        }

        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Error(errors)
    }

    fun validateRegistrationInput(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        val errors = mutableMapOf<String, String>()

        when {
            fullName.isEmpty() -> errors["name"] = "Full name cannot be empty"
            fullName.length < 2 -> errors["name"] = "Full name is too short"
        }

        when {
            email.isEmpty() -> errors["email"] = "Email cannot be empty"
            !email.isValidEmail() -> errors["email"] = "Invalid email format"
        }

        when {
            password.isEmpty() -> errors["password"] = "Password cannot be empty"
            !password.isValidPassword() -> errors["password"] =
                "Password must be between 6-20 characters"
        }

        when {
            confirmPassword.isEmpty() -> errors["confirmPassword"] =
                "Confirm password cannot be empty"

            password != confirmPassword -> errors["confirmPassword"] = "Passwords do not match"
        }

        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Error(errors)
    }

    fun validatePaymentInput(
        cardNumber: String,
        expiryDate: String,
        cvv: String,
        cardholderName: String
    ): ValidationResult {
        val errors = mutableMapOf<String, String>()

        when {
            cardNumber.isEmpty() -> errors["cardNumber"] = "Card number cannot be empty"
            !cardNumber.isValidCardNumber() -> errors["cardNumber"] = "Invalid card number format"

        }
        when {
            cardholderName.isEmpty() -> errors["cardholderName"] = "Cardholder name cannot be empty"
        }
        when {
            expiryDate.isEmpty() -> errors["expiryDate"] = "Expiry date cannot be empty"
            !expiryDate.isValidExpiryDate() -> errors["expiryDate"] = "Invalid expiry date format"
        }
        when {
            cvv.isEmpty() -> errors["cvv"] = "CVV cannot be empty"
            !cvv.isValidCvv() -> errors["cvv"] = "Invalid CVV format"
        }

        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Error(errors)
    }

    sealed class ValidationResult {
        data object Success : ValidationResult()
        data class Error(
            val errors: Map<String, String> = emptyMap(),
            val message: String = errors.values.firstOrNull() ?: "Validation failed"
        ) : ValidationResult() {
            constructor(message: String) : this(mapOf("general" to message), message)
        }
    }

}