package com.hasan.eventapp.domain.repositories

import com.hasan.eventapp.data.models.User
import com.hasan.eventapp.domain.models.UserDomain

interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<UserDomain>
    suspend fun register(user: User): Result<UserDomain>
}