package com.hasan.eventapp.domain.usecases.auth

import com.hasan.eventapp.data.models.User
import com.hasan.eventapp.domain.models.UserDomain
import com.hasan.eventapp.domain.repositories.IAuthRepository
import com.hasan.eventapp.utils.InputValidationException
import com.hasan.eventapp.utils.ValidationUtils
import java.util.UUID
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        name: String
    ): Result<UserDomain> {
        // Validate all input fields
        val validationResult = ValidationUtils.validateRegistrationInput(
            fullName = name,
            email = email,
            password = password,
            confirmPassword = confirmPassword
        )
        if (validationResult is ValidationUtils.ValidationResult.Error) {
            return Result.failure(InputValidationException(validationResult.errors))
        }

        val user = User(
            id = UUID.randomUUID().toString(),
            email = email,
            password = password,
            name = name
        )
        return authRepository.register(user)
    }
}