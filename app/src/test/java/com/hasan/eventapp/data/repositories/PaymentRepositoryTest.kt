package com.hasan.eventapp.data.repositories

import com.hasan.eventapp.data.models.Payment
import com.hasan.eventapp.data.remote.MockApiService
import com.hasan.eventapp.domain.models.PaymentDomain
import com.hasan.eventapp.domain.repositories.IPaymentRepository
import com.hasan.eventapp.utils.NetworkUtils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PaymentRepositoryTest {

    private lateinit var apiService: MockApiService
    private lateinit var networkUtils: NetworkUtils
    private lateinit var paymentRepository: IPaymentRepository

    private val testPayment = Payment(
        id = "payment-id",
        eventId = "event-id",
        amount = 99.99f,
        transactionId = "tx-123456",
        status = "COMPLETED",
        timestamp = 1633987654321
    )

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
        apiService = mockk()
        networkUtils = mockk()
        paymentRepository = PaymentRepository(apiService, networkUtils)
    }

    @Test
    fun `processPayment should return payment when network is available`() = runTest {
        // Arrange
        every { networkUtils.isNetworkAvailable() } returns true
        coEvery {
            apiService.processPayment(
                eventId = "event-id",
                amount = 99.99f,
                cardNumber = "4111111111111111",
                cardHolderName = "Test User"
            )
        } returns Result.success(testPayment)

        // Act
        val result = paymentRepository.processPayment(
            eventId = "event-id",
            amount = 99.99f,
            cardNumber = "4111111111111111",
            cardHolderName = "Test User"
        )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(testPaymentDomain, result.getOrNull())
        coVerify {
            apiService.processPayment(
                eventId = "event-id",
                amount = 99.99f,
                cardNumber = "4111111111111111",
                cardHolderName = "Test User"
            )
        }
    }

    @Test
    fun `processPayment should return error when network is unavailable`() = runTest {
        // Arrange
        every { networkUtils.isNetworkAvailable() } returns false

        // Act
        val result = paymentRepository.processPayment(
            eventId = "event-id",
            amount = 99.99f,
            cardNumber = "4111111111111111",
            cardHolderName = "Test User"
        )

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Internet connection required") ?: false)
    }

    @Test
    fun `processPayment should propagate API errors`() = runTest {
        // Arrange
        val error = Exception("Payment processing failed")
        every { networkUtils.isNetworkAvailable() } returns true
        coEvery {
            apiService.processPayment(
                eventId = "event-id",
                amount = 99.99f,
                cardNumber = "4111111111111111",
                cardHolderName = "Test User"
            )
        } returns Result.failure(error)

        // Act
        val result = paymentRepository.processPayment(
            eventId = "event-id",
            amount = 99.99f,
            cardNumber = "4111111111111111",
            cardHolderName = "Test User"
        )

        // Assert
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}