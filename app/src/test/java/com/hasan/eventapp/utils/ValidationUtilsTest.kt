package com.hasan.eventapp.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun `validateLoginInput should return Success for valid inputs`() {
        // Act
        val result = ValidationUtils.validateLoginInput(
            email = "test@example.com",
            password = "password123"
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Success)
    }

    @Test
    fun `validateLoginInput should return Error for empty email`() {
        // Act
        val result = ValidationUtils.validateLoginInput(
            email = "",
            password = "password123"
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Error)
        val errorResult = result as ValidationUtils.ValidationResult.Error
        assertEquals("Email cannot be empty", errorResult.errors["email"])
    }

    @Test
    fun `validateLoginInput should return Error for invalid email format`() {
        // Act
        val result = ValidationUtils.validateLoginInput(
            email = "invalid-email",
            password = "password123"
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Error)
        val errorResult = result as ValidationUtils.ValidationResult.Error
        assertEquals("Invalid email format", errorResult.errors["email"])
    }

    @Test
    fun `validateLoginInput should return Error for empty password`() {
        // Act
        val result = ValidationUtils.validateLoginInput(
            email = "test@example.com",
            password = ""
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Error)
        val errorResult = result as ValidationUtils.ValidationResult.Error
        assertEquals("Password cannot be empty", errorResult.errors["password"])
    }

    @Test
    fun `validateLoginInput should return Error for short password`() {
        // Act
        val result = ValidationUtils.validateLoginInput(
            email = "test@example.com",
            password = "pass"
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Error)
        val errorResult = result as ValidationUtils.ValidationResult.Error
        assertEquals("Password must be between 6-20 characters", errorResult.errors["password"])
    }

    @Test
    fun `validateRegistrationInput should return Success for valid inputs`() {
        // Act
        val result = ValidationUtils.validateRegistrationInput(
            fullName = "Test User",
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123"
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Success)
    }

    @Test
    fun `validateRegistrationInput should return Error for empty name`() {
        // Act
        val result = ValidationUtils.validateRegistrationInput(
            fullName = "",
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123"
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Error)
        val errorResult = result as ValidationUtils.ValidationResult.Error
        assertEquals("Full name cannot be empty", errorResult.errors["name"])
    }

    @Test
    fun `validateRegistrationInput should return Error for mismatched passwords`() {
        // Act
        val result = ValidationUtils.validateRegistrationInput(
            fullName = "Test User",
            email = "test@example.com",
            password = "password123",
            confirmPassword = "different_password"
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Error)
        val errorResult = result as ValidationUtils.ValidationResult.Error
        assertEquals("Passwords do not match", errorResult.errors["confirmPassword"])
    }

    @Test
    fun `validatePaymentInput should return Success for valid inputs`() {
        // Act
        val result = ValidationUtils.validatePaymentInput(
            cardNumber = "4111111111111111",
            expiryDate = "12/25",
            cvv = "123",
            cardholderName = "Test User"
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Success)
    }

    @Test
    fun `validatePaymentInput should return Error for invalid card number`() {
        // Act
        val result = ValidationUtils.validatePaymentInput(
            cardNumber = "1234",
            expiryDate = "12/25",
            cvv = "123",
            cardholderName = "Test User"
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Error)
        val errorResult = result as ValidationUtils.ValidationResult.Error
        assertEquals("Invalid card number format", errorResult.errors["cardNumber"])
    }

    @Test
    fun `validatePaymentInput should return Error for invalid expiry date`() {
        // Act
        val result = ValidationUtils.validatePaymentInput(
            cardNumber = "4111111111111111",
            expiryDate = "13/25", // Invalid month
            cvv = "123",
            cardholderName = "Test User"
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Error)
        val errorResult = result as ValidationUtils.ValidationResult.Error
        assertEquals("Invalid expiry date format", errorResult.errors["expiryDate"])
    }

    @Test
    fun `validatePaymentInput should return Error for invalid CVV`() {
        // Act
        val result = ValidationUtils.validatePaymentInput(
            cardNumber = "4111111111111111",
            expiryDate = "12/25",
            cvv = "12", // Too short
            cardholderName = "Test User"
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Error)
        val errorResult = result as ValidationUtils.ValidationResult.Error
        assertEquals("Invalid CVV format", errorResult.errors["cvv"])
    }

    @Test
    fun `validatePaymentInput should return Error for empty cardholder name`() {
        // Act
        val result = ValidationUtils.validatePaymentInput(
            cardNumber = "4111111111111111",
            expiryDate = "12/25",
            cvv = "123",
            cardholderName = ""
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Error)
        val errorResult = result as ValidationUtils.ValidationResult.Error
        assertEquals("Cardholder name cannot be empty", errorResult.errors["cardholderName"])
    }

    @Test
    fun `validatePaymentInput should return multiple errors when multiple fields are invalid`() {
        // Act
        val result = ValidationUtils.validatePaymentInput(
            cardNumber = "1234",
            expiryDate = "invalid",
            cvv = "12",
            cardholderName = ""
        )

        // Assert
        assertTrue(result is ValidationUtils.ValidationResult.Error)
        val errorResult = result as ValidationUtils.ValidationResult.Error
        assertEquals("Invalid card number format", errorResult.errors["cardNumber"])
        assertEquals("Invalid expiry date format", errorResult.errors["expiryDate"])
        assertEquals("Invalid CVV format", errorResult.errors["cvv"])
        assertEquals("Cardholder name cannot be empty", errorResult.errors["cardholderName"])
    }
}