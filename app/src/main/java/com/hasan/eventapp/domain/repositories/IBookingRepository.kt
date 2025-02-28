package com.hasan.eventapp.domain.repositories

import com.hasan.eventapp.domain.models.BookingDomain
import kotlinx.coroutines.flow.Flow

interface IBookingRepository {
    fun createBooking(
        eventId: String,
        userId: String,
        paymentId: String
    ): Flow<Result<BookingDomain>>
}