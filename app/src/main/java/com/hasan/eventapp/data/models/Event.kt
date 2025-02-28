package com.hasan.eventapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hasan.eventapp.domain.models.EventDomain

@Entity(tableName = "EVENT")
data class Event(
    @PrimaryKey
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
) {
    fun toDomain() = EventDomain(
        id = id,
        title = title,
        description = description,
        shortDescription = shortDescription,
        imageUrl = imageUrl,
        date = date,
        time = time,
        location = location,
        price = price,
        availableSeats = availableSeats
    )
}