package com.hasan.eventapp.data.repositories

import app.cash.turbine.test
import com.hasan.eventapp.data.models.Booking
import com.hasan.eventapp.data.remote.MockApiService
import com.hasan.eventapp.domain.models.BookingDomain
import com.hasan.eventapp.domain.repositories.IBookingRepository
import com.hasan.eventapp.utils.NetworkUtils
import com.hasan.eventapp.utils.test.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class BookingRepositoryTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var apiService: MockApiService
    private lateinit var networkUtils: NetworkUtils
    private lateinit var bookingRepository: IBookingRepository

    private val testBooking = Booking(
        id = "booking-id",
        eventId = "event-id",
        userId = "user-id",
        bookingDate = 1633987654321,
        paymentId = "payment-id",
        bookingReference = "BK-ABCD1234",
        status = "CONFIRMED"
    )

    private val testBookingDomain = BookingDomain(
        id = "booking-id",
        eventId = "event-id",
        userId = "user-id",
        bookingDate = 1633987654321,
        paymentId = "payment-id",
        bookingReference = "BK-ABCD1234",
        status = "CONFIRMED"
    )

    @Before
    fun setup() {
        apiService = mockk()
        networkUtils = mockk()
        bookingRepository = BookingRepository(apiService, networkUtils)
    }

    @Test
    fun `createBooking should return booking when network is available`() = runTest {
        // Arrange
        every { networkUtils.isNetworkAvailable() } returns true
        coEvery {
            apiService.createBooking(
                eventId = "event-id",
                userId = "user-id",
                paymentId = "payment-id"
            )
        } returns Result.success(testBooking)

        // Act & Assert
        bookingRepository.createBooking(
            eventId = "event-id",
            userId = "user-id",
            paymentId = "payment-id"
        ).test {
            val result = awaitItem()

            // Assert the result
            assertTrue(result.isSuccess)
            assertEquals(testBookingDomain, result.getOrNull())

            // Verify API service was called
            coVerify {
                apiService.createBooking(
                    eventId = "event-id",
                    userId = "user-id",
                    paymentId = "payment-id"
                )
            }

            awaitComplete()
        }
    }

    @Test
    fun `createBooking should return error when network is unavailable`() = runTest {
        // Arrange
        every { networkUtils.isNetworkAvailable() } returns false

        // Act & Assert
        bookingRepository.createBooking(
            eventId = "event-id",
            userId = "user-id",
            paymentId = "payment-id"
        ).test {
            val result = awaitItem()

            // Assert the result
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("Internet connection required") ?: false)

            awaitComplete()
        }
    }

    @Test
    fun `createBooking should propagate API errors`() = runTest {
        // Arrange
        val error = Exception("Booking creation failed")
        every { networkUtils.isNetworkAvailable() } returns true
        coEvery {
            apiService.createBooking(
                eventId = "event-id",
                userId = "user-id",
                paymentId = "payment-id"
            )
        } returns Result.failure(error)

        // Act & Assert
        bookingRepository.createBooking(
            eventId = "event-id",
            userId = "user-id",
            paymentId = "payment-id"
        ).test {
            val result = awaitItem()

            // Assert the result
            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())

            awaitComplete()
        }
    }
}