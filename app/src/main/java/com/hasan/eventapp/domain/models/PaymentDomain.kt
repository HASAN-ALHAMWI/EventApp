package com.hasan.eventapp.domain.models

data class PaymentDomain(
    val id: String,
    val eventId: String,
    val amount: Float,
    val transactionId: String,
    val status: String,
    val timestamp: Long
)