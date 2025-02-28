package com.hasan.eventapp.data.repositories

import com.hasan.eventapp.data.models.User
import com.hasan.eventapp.data.remote.MockApiService
import com.hasan.eventapp.domain.models.UserDomain
import com.hasan.eventapp.domain.repositories.IAuthRepository
import com.hasan.eventapp.utils.NetworkUtils
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: MockApiService,
    private val networkUtils: NetworkUtils
): IAuthRepository {
    override suspend fun login(email: String, password: String): Result<UserDomain> {
        return if (networkUtils.isNetworkAvailable()) {
            apiService.login(email, password).map { it.toDomain() }
        } else {
            Result.failure(Exception("Internet connection required for login"))
        }
    }

    override suspend fun register(user: User): Result<UserDomain> {
        return if (networkUtils.isNetworkAvailable()) {
            apiService.register(user).map { it.toDomain() }
        } else {
            Result.failure(Exception("Internet connection required for registration"))
        }
    }
}