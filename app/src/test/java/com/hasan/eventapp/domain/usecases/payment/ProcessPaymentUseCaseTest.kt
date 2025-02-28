package com.hasan.eventapp.domain.usecases.payment

import com.hasan.eventapp.data.repositories.PaymentRepository
import com.hasan.eventapp.domain.models.PaymentDomain
import com.hasan.eventapp.utils.InputValidationException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProcessPaymentUseCaseTest {

    private lateinit var paymentRepository: PaymentRepository
    private lateinit var processPaymentUseCase: ProcessPaymentUseCase


    private val testPaymentDomain = PaymentDomain(
        id = "payment-id",
        eventId = "event-id",
        amount = 99.99f,
        transactionId = "tx-123456",
        status = "COMPLETED",
        timestamp = 1633987654321
    )

    @Before
    fun setup() {
        paymentRepository = mockk()
        processPaymentUseCase = ProcessPaymentUseCase(paymentRepository)
    }

    @Test
    fun `invoke with valid payment details should return success`() = runTest {
        // Arrange
        coEvery {
            paymentRepository.processPayment(
                eventId = "event-id",
                amount = 99.99f,
                cardNumber = "4111111111111111",
                cardHolderName = "Test User"
            )
        } returns Result.success(testPaymentDomain)

        // Act
        val result = processPaymentUseCase(
            eventId = "event-id",
            cardNumber = "4111111111111111",
            amount = 99.99f,
            cardHolderName = "Test User",
            cvv = "123",
            expiryDate = "12/25"
        )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(testPaymentDomain, result.getOrNull())
        coVerify {
            paymentRepository.processPayment(
                eventId = "event-id",
                amount = 99.99f,
                cardNumber = "4111111111111111",
                cardHolderName = "Test User"
            )
        }
    }

    @Test
    fun `invoke with invalid card number should return validation error`() = runTest {
        // Act
        val result = processPaymentUseCase(
            eventId = "event-id",
            cardNumber = "1234",
            amount = 99.99f,
            cardHolderName = "Test User",
            cvv = "123",
            expiryDate = "12/25"
        )

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InputValidationException)
        val exception = result.exceptionOrNull() as InputValidationException
        assertEquals("Invalid card number format", exception.getError("cardNumber"))
    }

    @Test
    fun `invoke with invalid cvv should return validation error`() = runTest {
        // Act
        val result = processPaymentUseCase(
            eventId = "event-id",
            cardNumber = "4111111111111111",
            amount = 99.99f,
            cardHolderName = "Test User",
            cvv = "12",
            expiryDate = "12/25"
        )

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InputValidationException)
        val exception = result.exceptionOrNull() as InputValidationException
        assertEquals("Invalid CVV format", exception.getError("cvv"))
    }

    @Test
    fun `invoke with invalid expiry date should return validation error`() = runTest {
        // Act
        val result = processPaymentUseCase(
            eventId = "event-id",
            cardNumber = "4111111111111111",
            amount = 99.99f,
            cardHolderName = "Test User",
            cvv = "123",
            expiryDate = "13/25" // Invalid month
        )

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InputValidationException)
        val exception = result.exceptionOrNull() as InputValidationException
        assertEquals("Invalid expiry date format", exception.getError("expiryDate"))
    }

    @Test
    fun `invoke with empty cardholder name should return validation error`() = runTest {
        // Act
        val result = processPaymentUseCase(
            eventId = "event-id",
            cardNumber = "4111111111111111",
            amount = 99.99f,
            cardHolderName = "",
            cvv = "123",
            expiryDate = "12/25"
        )

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InputValidationException)
        val exception = result.exceptionOrNull() as InputValidationException
        assertEquals("Cardholder name cannot be empty", exception.getError("cardholderName"))
    }

    @Test
    fun `invoke with payment failure should propagate error`() = runTest {
        // Arrange
        val error = Exception("Payment processing failed")
        coEvery {
            paymentRepository.processPayment(
                eventId = "event-id",
                amount = 99.99f,
                cardNumber = "4111111111111111",
                cardHolderName = "Test User"
            )
        } returns Result.failure(error)

        // Act
        val result = processPaymentUseCase(
            eventId = "event-id",
            cardNumber = "4111111111111111",
            amount = 99.99f,
            cardHolderName = "Test User",
            cvv = "123",
            expiryDate = "12/25"
        )

        // Assert
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}