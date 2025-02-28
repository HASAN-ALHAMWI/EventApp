package com.hasan.eventapp.data.models

import com.hasan.eventapp.domain.models.PaymentDomain

data class Payment(
    val id: String,
    val eventId: String,
    val amount: Float,
    val transactionId: String,
    val status: String,
    val timestamp: Long
) {
    fun toDomain() = PaymentDomain(
        id = id,
        eventId = eventId,
        amount = amount,
        transactionId = transactionId,
        status = status,
        timestamp = timestamp
    )
}
