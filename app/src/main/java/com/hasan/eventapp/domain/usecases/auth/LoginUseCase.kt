package com.hasan.eventapp.domain.usecases.auth

import com.hasan.eventapp.domain.models.UserDomain
import com.hasan.eventapp.domain.repositories.IAuthRepository
import com.hasan.eventapp.utils.InputValidationException
import com.hasan.eventapp.utils.SessionManager
import com.hasan.eventapp.utils.ValidationUtils
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(email: String, password: String): Result<UserDomain> {
        // Validate input before making a network call
        val validationResult = ValidationUtils.validateLoginInput(email, password)
        if (validationResult is ValidationUtils.ValidationResult.Error) {
            return Result.failure(InputValidationException(validationResult.errors))
        }

        // Perform login operation
        return authRepository.login(email, password).map { userDomain ->
            // Save session on successful login
            sessionManager.saveUserSession(userDomain)
            userDomain
        }
    }

    fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    fun getCurrentUser(): UserDomain? {
        return sessionManager.getCurrentUser()
    }
}