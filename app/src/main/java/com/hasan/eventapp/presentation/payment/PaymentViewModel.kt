package com.hasan.eventapp.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.eventapp.data.managers.UserManager
import com.hasan.eventapp.domain.models.BookingDomain
import com.hasan.eventapp.domain.usecases.booking.CreateBookingUseCase
import com.hasan.eventapp.domain.usecases.payment.ProcessPaymentUseCase
import com.hasan.eventapp.utils.InputValidationException
import com.hasan.eventapp.utils.extensions.formatCardNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val userManager: UserManager
) : ViewModel() {

    // ===========================
    // State Management
    // ===========================
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Initial)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    private val _createBookingState = MutableStateFlow<CreateBookingState?>(null)
    val createBookingState: StateFlow<CreateBookingState?> = _createBookingState.asStateFlow()

    // ===========================
    // Public Methods
    // ===========================
    fun resetState() {
        _paymentState.value = PaymentState.Initial
    }

    fun processPayment(
        eventId: String,
        cardNumber: String,
        cardholderName: String,
        amount: Float,
        expiryDate: String,
        cvv: String,
    ) {
        viewModelScope.launch {
            emitLoadingState()

            processPaymentUseCase(
                eventId = eventId,
                cardNumber = cardNumber.formatCardNumber(),
                amount = amount,
                cardHolderName = cardholderName,
                cvv = cvv,
                expiryDate = expiryDate
            ).fold(
                onSuccess = { result ->
                    handlePaymentSuccess(
                        paymentId = result.transactionId,
                        eventId = result.eventId,
                        userId = userManager.getCurrentUserId()
                    )
                },
                onFailure = { exception ->
                    handlePaymentFailure(exception)
                }
            )
        }
    }

    // ===========================
    // Payment Process Handlers
    // ===========================
    private fun emitLoadingState() {
        _paymentState.value = PaymentState.Processing
    }

    private fun handlePaymentSuccess(eventId: String, paymentId: String, userId: String) {
        createBooking(eventId, paymentId, userId)
    }

    private fun handlePaymentFailure(exception: Throwable) {
        when (exception) {
            is InputValidationException -> handleValidationError(exception)
            else -> handleApiError(exception)
        }
    }

    private fun handleValidationError(exception: InputValidationException) {
        _paymentState.value = PaymentState.ValidationError(
            cardNumberError = exception.getError("cardNumber"),
            expiryDateError = exception.getError("expiryDate"),
            cvvError = exception.getError("cvv"),
            cardholderNameError = exception.getError("cardholderName")
        )
    }

    private fun handleApiError(exception: Throwable) {
        _paymentState.value = PaymentState.ApiError(
            exception.message ?: "Payment processing failed"
        )
    }

    // ===========================
    // Booking Process
    // ===========================
    private fun createBooking(eventId: String, paymentId: String, userId: String) {
        viewModelScope.launch {
            createBookingUseCase(
                eventId = eventId,
                userId = userId,
                paymentId = paymentId
            ).catch { exception ->
                handleBookingError(exception)
            }.collect { result ->
                result.fold(
                    onSuccess = { booking -> handleBookingSuccess(booking) },
                    onFailure = { exception -> handleBookingError(exception) }
                )
            }
        }
    }

    private fun handleBookingSuccess(booking: BookingDomain) {
        _createBookingState.value = CreateBookingState.Success(booking)
    }

    private fun handleBookingError(exception: Throwable) {
        _createBookingState.value = CreateBookingState.Error(
            exception.message ?: "Booking creation failed"
        )
    }
}

sealed class PaymentState {
    data object Initial : PaymentState()
    data object Processing : PaymentState()
    data class ValidationError(
        val cardNumberError: String? = null,
        val expiryDateError: String? = null,
        val cvvError: String? = null,
        val cardholderNameError: String? = null
    ) : PaymentState()

    data class ApiError(val message: String) : PaymentState()
}

sealed class CreateBookingState {
    data class Success(val booking: BookingDomain) : CreateBookingState()
    data class Error(val message: String) : CreateBookingState()
}