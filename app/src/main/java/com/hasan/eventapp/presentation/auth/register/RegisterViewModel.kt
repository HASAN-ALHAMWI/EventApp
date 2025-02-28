package com.hasan.eventapp.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.eventapp.domain.models.UserDomain
import com.hasan.eventapp.domain.usecases.auth.RegisterUseCase
import com.hasan.eventapp.utils.InputValidationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    // ===========================
    // State Management
    // ===========================
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Initial)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    // ===========================
    // Public Methods
    // ===========================
    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            emitLoadingState()

            registerUseCase(email, password, confirmPassword, name)
                .fold(
                    onSuccess = { user -> handleRegistrationSuccess(user) },
                    onFailure = { exception -> handleRegistrationFailure(exception) }
                )
        }
    }

    fun resetState() {
        _registrationState.value = RegistrationState.Initial
    }

    // ===========================
    // Private Helper Methods
    // ===========================
    private fun emitLoadingState() {
        _registrationState.value = RegistrationState.Loading
    }

    private fun handleRegistrationSuccess(user: UserDomain) {
        _registrationState.value = RegistrationState.Success(user)
    }

    private fun handleRegistrationFailure(exception: Throwable) {
        when (exception) {
            is InputValidationException -> handleValidationError(exception)
            else -> handleApiError(exception)
        }
    }

    private fun handleValidationError(exception: InputValidationException) {
        _registrationState.value = RegistrationState.ValidationError(
            nameError = exception.getError("name"),
            emailError = exception.getError("email"),
            passwordError = exception.getError("password"),
            confirmPasswordError = exception.getError("confirmPassword")
        )
    }

    private fun handleApiError(exception: Throwable) {
        _registrationState.value = RegistrationState.ApiError(
            exception.message ?: "Registration failed"
        )
    }
}

sealed class RegistrationState {
    data object Initial : RegistrationState()
    data object Loading : RegistrationState()
    data class Success(val user: UserDomain) : RegistrationState()
    data class ValidationError(
        val nameError: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null
    ) : RegistrationState()

    data class ApiError(val message: String) : RegistrationState()
}