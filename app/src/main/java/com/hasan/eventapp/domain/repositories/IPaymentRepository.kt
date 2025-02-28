package com.hasan.eventapp.domain.repositories

import com.hasan.eventapp.domain.models.PaymentDomain

interface IPaymentRepository {
    suspend fun processPayment(
        eventId: String,
        amount: Float,
        cardNumber: String,
        cardHolderName: String
    ): Result<PaymentDomain>
}