package com.hasan.eventapp.domain.models

data class EventDomain(
    val id: String,
    val title: String,
    val description: String,
    val shortDescription: String,
    val imageUrl: String,
    val date: String,
    val time: String,
    val location: String,
    val price: Float,
    val availableSeats: Int
)