package com.hasan.eventapp.data.repositories

import com.hasan.eventapp.data.remote.MockApiService
import com.hasan.eventapp.domain.models.BookingDomain
import com.hasan.eventapp.domain.repositories.IBookingRepository
import com.hasan.eventapp.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookingRepository @Inject constructor(
    private val apiService: MockApiService,
    private val networkUtils: NetworkUtils
) : IBookingRepository {
    override fun createBooking(
        eventId: String, userId: String, paymentId: String
    ): Flow<Result<BookingDomain>> = flow {
        if (networkUtils.isNetworkAvailable()) {
            try {
                val result = apiService.createBooking(
                    eventId = eventId,
                    userId = userId,
                    paymentId = paymentId
                )
                emit(result.map { it.toDomain() })
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        } else {
            emit(Result.failure(Exception("Internet connection required for payment processing")))
        }
    }
}