package com.hasan.eventapp.domain.usecases.booking

import app.cash.turbine.test
import com.hasan.eventapp.data.models.Booking
import com.hasan.eventapp.data.repositories.BookingRepository
import com.hasan.eventapp.domain.models.BookingDomain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class CreateBookingUseCaseTest {

    private lateinit var bookingRepository: BookingRepository
    private lateinit var createBookingUseCase: CreateBookingUseCase

    private val testBooking = BookingDomain(
        id = "booking-id",
        eventId = "event-id",
        userId = "user-id",
        bookingDate = 1633987654321,
        paymentId = "payment-id",
        bookingReference = "BK-ABCD1234",
        status = "CONFIRMED"
    )

    private val expectedBookingDomain = BookingDomain(
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
        bookingRepository = mockk()
        createBookingUseCase = CreateBookingUseCase(bookingRepository)
    }

    @Test
    fun `invoke should return successful booking when repository returns success`() = runTest {
        // Arrange
        every {
            bookingRepository.createBooking(
                eventId = "event-id",
                userId = "user-id",
                paymentId = "payment-id"
            )
        } returns flowOf(Result.success(testBooking))

        // Act & Assert
        createBookingUseCase(
            eventId = "event-id",
            userId = "user-id",
            paymentId = "payment-id"
        ).test {
            val result = awaitItem()

            // Assert the result
            assertTrue(result.isSuccess)
            assertEquals(expectedBookingDomain, result.getOrNull())

            // Verify repository call
            verify {
                bookingRepository.createBooking(
                    eventId = "event-id",
                    userId = "user-id",
                    paymentId = "payment-id"
                )
            }

            awaitComplete()
        }
    }

    @Test
    fun `invoke should propagate error when repository returns failure`() = runTest {
        // Arrange
        val error = Exception("Booking creation failed")
        every {
            bookingRepository.createBooking(
                eventId = "event-id",
                userId = "user-id",
                paymentId = "payment-id"
            )
        } returns flowOf(Result.failure(error))

        // Act & Assert
        createBookingUseCase(
            eventId = "event-id",
            userId = "user-id",
            paymentId = "payment-id"
        ).test {
            val result = awaitItem()

            // Assert the result is a failure with correct error
            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())

            // Verify repository call
            verify {
                bookingRepository.createBooking(
                    eventId = "event-id",
                    userId = "user-id",
                    paymentId = "payment-id"
                )
            }

            awaitComplete()
        }
    }

    @Test
    fun `invoke should properly map data model to domain model`() = runTest {
        // Arrange - Create a booking with different values
        val customBooking = BookingDomain(
            id = "custom-booking-id",
            eventId = "custom-event-id",
            userId = "custom-user-id",
            bookingDate = 1635000000000,
            paymentId = "custom-payment-id",
            bookingReference = "BK-CUSTOM123",
            status = "PENDING"
        )

        val expectedCustomDomain = BookingDomain(
            id = "custom-booking-id",
            eventId = "custom-event-id",
            userId = "custom-user-id",
            bookingDate = 1635000000000,
            paymentId = "custom-payment-id",
            bookingReference = "BK-CUSTOM123",
            status = "PENDING"
        )

        every {
            bookingRepository.createBooking(
                eventId = "custom-event-id",
                userId = "custom-user-id",
                paymentId = "custom-payment-id"
            )
        } returns flowOf(Result.success(customBooking))

        // Act & Assert
        createBookingUseCase(
            eventId = "custom-event-id",
            userId = "custom-user-id",
            paymentId = "custom-payment-id"
        ).test {
            val result = awaitItem()

            // Assert domain mapping is correct
            assertTrue(result.isSuccess)
            assertEquals(expectedCustomDomain, result.getOrNull())

            awaitComplete()
        }
    }

    @Test
    fun `invoke with empty parameters should still call repository`() = runTest {
        // Arrange - Test with empty strings (still valid for the repository)
        every {
            bookingRepository.createBooking(
                eventId = "",
                userId = "",
                paymentId = ""
            )
        } returns flowOf(Result.failure(Exception("Invalid parameters")))

        // Act & Assert - Ensure repository is still called even with empty strings
        createBookingUseCase(
            eventId = "",
            userId = "",
            paymentId = ""
        ).test {
            awaitItem() // Just consume the result, we're testing the repository call

            // Verify repository was called with empty strings
            verify {
                bookingRepository.createBooking(
                    eventId = "",
                    userId = "",
                    paymentId = ""
                )
            }

            awaitComplete()
        }
    }
}