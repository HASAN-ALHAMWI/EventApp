package com.hasan.eventapp.presentation.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.hasan.eventapp.data.managers.UserManager
import com.hasan.eventapp.domain.models.BookingDomain
import com.hasan.eventapp.domain.models.PaymentDomain
import com.hasan.eventapp.domain.usecases.booking.CreateBookingUseCase
import com.hasan.eventapp.domain.usecases.payment.ProcessPaymentUseCase
import com.hasan.eventapp.utils.InputValidationException
import com.hasan.eventapp.utils.test.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class PaymentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var processPaymentUseCase: ProcessPaymentUseCase
    private lateinit var createBookingUseCase: CreateBookingUseCase
    private lateinit var userManager: UserManager
    private lateinit var viewModel: PaymentViewModel

    private val testPayment = PaymentDomain(
        id = "payment-id",
        eventId = "event-id",
        amount = 99.99f,
        transactionId = "tx-123456",
        status = "COMPLETED",
        timestamp = 1633987654321
    )

    private val testBooking = BookingDomain(
        id = "booking-id",
        eventId = "event-id",
        userId = "user-id",
        bookingDate = 1633987654321,
        paymentId = "tx-123456",
        bookingReference = "BK-ABCD1234",
        status = "CONFIRMED"
    )

    @Before
    fun setup() {
        processPaymentUseCase = mockk()
        createBookingUseCase = mockk()
        userManager = mockk()

        every { userManager.getCurrentUserId() } returns "user-id"

        viewModel = PaymentViewModel(processPaymentUseCase, createBookingUseCase, userManager)
    }

    @Test
    fun `processPayment should emit Processing then Success states when successful`() = runTest {
        // Arrange
        coEvery {
            processPaymentUseCase.invoke(
                eventId = "event-id",
                cardNumber = "4111111111111111",
                amount = 99.99f,
                cardHolderName = "Test User",
                cvv = "123",
                expiryDate = "12/25"
            )
        } returns Result.success(testPayment)

        coEvery {
            createBookingUseCase.invoke(
                eventId = "event-id",
                paymentId = "tx-123456",
                userId = "user-id"
            )
        } returns flowOf(Result.success(testBooking))

        // Act & Assert - Payment State
        viewModel.paymentState.test {
            assertEquals(PaymentState.Initial, awaitItem())

            viewModel.processPayment(
                eventId = "event-id",
                cardNumber = "4111111111111111",
                cardholderName = "Test User",
                amount = 99.99f,
                expiryDate = "12/25",
                cvv = "123"
            )

            assertEquals(PaymentState.Processing, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        // Check Booking State
        viewModel.createBookingState.test {
            assertEquals(CreateBookingState.Success(testBooking), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `processPayment should emit ValidationError state for invalid card details`() = runTest {
        // Arrange
        val validationErrors = mapOf(
            "cardNumber" to "Invalid card number format",
            "cardholderName" to "Cardholder name cannot be empty"
        )
        val exception = InputValidationException(validationErrors)

        coEvery {
            processPaymentUseCase.invoke(
                eventId = "event-id",
                cardNumber = "123",
                amount = 99.99f,
                cardHolderName = "",
                cvv = "123",
                expiryDate = "12/25"
            )
        } returns Result.failure(exception)

        // Create a test ViewModel
        val testViewModel =
            PaymentViewModel(processPaymentUseCase, createBookingUseCase, userManager)

        // Call the process payment function
        testViewModel.processPayment(
            eventId = "event-id",
            cardNumber = "123",
            cardholderName = "",
            amount = 99.99f,
            expiryDate = "12/25",
            cvv = "123"
        )

        // Wait for coroutines to complete
        advanceUntilIdle()

        // Get the current state directly
        val currentState = testViewModel.paymentState.value

        // Assert the state is ValidationError with correct values
        assertTrue(
            "Current state should be ValidationError",
            currentState is PaymentState.ValidationError
        )
        val errorState = currentState as PaymentState.ValidationError

        assertEquals("Invalid card number format", errorState.cardNumberError)
        assertEquals("Cardholder name cannot be empty", errorState.cardholderNameError)
    }

    @Test
    fun `processPayment should emit ApiError state when payment processing fails`() = runTest {
        // Arrange
        val error = Exception("Payment processing failed")

        coEvery {
            processPaymentUseCase.invoke(
                eventId = "event-id",
                cardNumber = "4111111111111111",
                amount = 99.99f,
                cardHolderName = "Test User",
                cvv = "123",
                expiryDate = "12/25"
            )
        } returns Result.failure(error)

        // Act & Assert
        viewModel.paymentState.test {
            assertEquals(PaymentState.Initial, awaitItem())

            viewModel.processPayment(
                eventId = "event-id",
                cardNumber = "4111111111111111",
                cardholderName = "Test User",
                amount = 99.99f,
                expiryDate = "12/25",
                cvv = "123"
            )

            assertEquals(PaymentState.Processing, awaitItem())

            val errorState = awaitItem() as PaymentState.ApiError
            assertEquals("Payment processing failed", errorState.message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `createBooking should emit Error state when booking fails`() = runTest {
        // Arrange
        val paymentId = "tx-12345"
        val eventId = "event-id"
        val userId = "user-id"
        val bookingError = Exception("Booking creation failed")

        // Setup successful payment
        val testPayment = PaymentDomain(
            id = "payment-id",
            eventId = eventId,
            amount = 99.99f,
            transactionId = paymentId,
            status = "COMPLETED",
            timestamp = 1633987654321
        )

        // Mock successful payment processing
        coEvery {
            processPaymentUseCase.invoke(
                eventId = eventId,
                cardNumber = any(),
                amount = any(),
                cardHolderName = any(),
                cvv = any(),
                expiryDate = any()
            )
        } returns Result.success(testPayment)

        // Mock failed booking creation
        every { userManager.getCurrentUserId() } returns userId

        // Create a test booking result flow that emits a failure
        val bookingFlow = MutableStateFlow<Result<BookingDomain>>(
            Result.failure(bookingError)
        )

        // Setup booking use case to return the flow
        coEvery {
            createBookingUseCase.invoke(
                eventId = eventId,
                paymentId = paymentId,
                userId = userId
            )
        } returns bookingFlow

        // Create a test ViewModel
        val testViewModel =
            PaymentViewModel(processPaymentUseCase, createBookingUseCase, userManager)

        // Trigger payment processing which should then create booking
        testViewModel.processPayment(
            eventId = eventId,
            cardNumber = "4111111111111111",
            cardholderName = "Test User",
            amount = 99.99f,
            expiryDate = "12/25",
            cvv = "123"
        )

        // Let coroutines complete
        advanceUntilIdle()

        // Get the current booking state directly
        val currentState = testViewModel.createBookingState.value

        // Assert booking state is Error with correct message
        assertNotNull("Booking state should not be null", currentState)
        assertTrue("Booking state should be Error", currentState is CreateBookingState.Error)
        assertEquals("Booking creation failed", (currentState as CreateBookingState.Error).message)
    }

    @Test
    fun `resetState should emit Initial state`() = runTest {
        // Arrange - set state to something other than Initial
        coEvery {
            processPaymentUseCase.invoke(any(), any(), any(), any(), any(), any())
        } returns Result.failure(Exception("Error"))

        viewModel.paymentState.test {
            assertEquals(PaymentState.Initial, awaitItem())

            viewModel.processPayment(
                eventId = "event-id",
                cardNumber = "123",
                cardholderName = "Test User",
                amount = 99.99f,
                expiryDate = "12/25",
                cvv = "123"
            )

            assertEquals(PaymentState.Processing, awaitItem())
            assertTrue(awaitItem() is PaymentState.ApiError)

            // Act
            viewModel.resetState()

            // Assert
            assertEquals(PaymentState.Initial, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}