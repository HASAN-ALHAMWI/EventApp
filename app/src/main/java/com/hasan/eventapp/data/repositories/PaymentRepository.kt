package com.hasan.eventapp.data.repositories

import com.hasan.eventapp.data.remote.MockApiService
import com.hasan.eventapp.domain.models.PaymentDomain
import com.hasan.eventapp.domain.repositories.IPaymentRepository
import com.hasan.eventapp.utils.NetworkUtils
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val apiService: MockApiService,
    private val networkUtils: NetworkUtils
) : IPaymentRepository {
    override suspend fun processPayment(
        eventId: String,
        amount: Float,
        cardNumber: String,
        cardHolderName: String
    ): Result<PaymentDomain> {
        return if (networkUtils.isNetworkAvailable()) {
            try {
                apiService.processPayment(
                    eventId = eventId,
                    amount = amount,
                    cardNumber = cardNumber,
                    cardHolderName = cardHolderName
                ).map { it.toDomain() }
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("Internet connection required for payment processing"))
        }
    }
}