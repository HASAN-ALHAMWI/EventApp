package com.hasan.eventapp.domain.usecases.booking

import com.hasan.eventapp.domain.models.BookingDomain
import com.hasan.eventapp.domain.repositories.IBookingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val bookingRepository: IBookingRepository
) {
    operator fun invoke(
        eventId: String,
        paymentId: String,
        userId: String
    ): Flow<Result<BookingDomain>> {
        return bookingRepository.createBooking(
            eventId = eventId,
            paymentId = paymentId,
            userId = userId
        )
    }
}