package com.hasan.eventapp.data.models

import com.hasan.eventapp.domain.models.BookingDomain

data class Booking(
    val id: String,
    val eventId: String,
    val userId: String,
    val bookingDate: Long,
    val paymentId: String,
    val bookingReference: String,
    val status: String
) {
    fun toDomain() = BookingDomain(
        id = id,
        eventId = eventId,
        userId = userId,
        bookingDate = bookingDate,
        paymentId = paymentId,
        bookingReference = bookingReference,
        status = status
    )
}
