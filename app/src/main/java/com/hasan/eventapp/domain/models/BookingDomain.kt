package com.hasan.eventapp.domain.models


data class BookingDomain(
    val id: String,
    val eventId: String,
    val userId: String,
    val bookingDate: Long,
    val paymentId: String,
    val bookingReference: String,
    val status: String
)
