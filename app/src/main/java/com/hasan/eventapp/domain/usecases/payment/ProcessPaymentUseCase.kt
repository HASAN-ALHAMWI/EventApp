package com.hasan.eventapp.domain.usecases.payment

import com.hasan.eventapp.data.repositories.PaymentRepository
import com.hasan.eventapp.domain.models.PaymentDomain
import com.hasan.eventapp.utils.InputValidationException
import com.hasan.eventapp.utils.ValidationUtils
import javax.inject.Inject

class ProcessPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
        eventId: String,
        cardNumber: String,
        amount: Float,
        cardHolderName: String,
        cvv: String,
        expiryDate: String
    ): Result<PaymentDomain> {
        // Validate input before making a network call
        val validationResult = ValidationUtils.validatePaymentInput(
            cardNumber = cardNumber,
            expiryDate = expiryDate,
            cvv = cvv,
            cardholderName = cardHolderName
        )
        if (validationResult is ValidationUtils.ValidationResult.Error) {
            return Result.failure(InputValidationException(validationResult.errors))
        }

        return paymentRepository.processPayment(
            eventId = eventId,
            amount = amount,
            cardNumber = cardNumber,
            cardHolderName = cardHolderName
        )
    }
}