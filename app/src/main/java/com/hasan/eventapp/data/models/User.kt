package com.hasan.eventapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hasan.eventapp.domain.models.UserDomain

@Entity(tableName = "USER")
data class User(
    @PrimaryKey
    val id: String,
    val email: String,
    val password: String,
    val name: String
) {
    fun toDomain() = UserDomain(
        id = id,
        email = email,
        name = name
    )
}