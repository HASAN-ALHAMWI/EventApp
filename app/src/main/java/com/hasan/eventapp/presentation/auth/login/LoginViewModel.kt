package com.hasan.eventapp.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.eventapp.data.managers.UserManager
import com.hasan.eventapp.domain.models.UserDomain
import com.hasan.eventapp.domain.usecases.auth.LoginUseCase
import com.hasan.eventapp.utils.InputValidationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val userManager: UserManager
) : ViewModel() {

    // ===========================
    // State Management
    // ===========================
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // ===========================
    // Lifecycle Methods
    // ===========================
    init {
        checkExistingSession()
    }

    // ===========================
    // Public Methods
    // ===========================
    fun login(email: String, password: String) {
        viewModelScope.launch {
            emitLoadingState()

            loginUseCase(email, password)
                .fold(
                    onSuccess = { user -> handleLoginSuccess(user) },
                    onFailure = { exception -> handleLoginFailure(exception) }
                )
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Initial
    }

    // ===========================
    // Private Helper Methods
    // ===========================
    private fun checkExistingSession() {
        if (loginUseCase.isUserLoggedIn()) {
            val user = loginUseCase.getCurrentUser()
            user?.let {
                emitSuccessState(it)
            }
        }
    }

    private fun emitLoadingState() {
        _loginState.value = LoginState.Loading
    }

    private fun handleLoginSuccess(user: UserDomain) {
        emitSuccessState(user)
    }

    private fun emitSuccessState(user: UserDomain) {
        userManager.setCurrentUser(user)
        _loginState.value = LoginState.Success(user)
    }

    private fun handleLoginFailure(exception: Throwable) {
        when (exception) {
            is InputValidationException -> handleValidationError(exception)
            else -> handleApiError(exception)
        }
    }

    private fun handleValidationError(exception: InputValidationException) {
        _loginState.value = LoginState.ValidationError(
            emailError = exception.getError("email"),
            passwordError = exception.getError("password"),
        )
    }

    private fun handleApiError(exception: Throwable) {
        _loginState.value = LoginState.ApiError(exception.message ?: "Login failed")
    }
}

sealed class LoginState {
    data object Initial : LoginState()
    data object Loading : LoginState()
    data class Success(val user: UserDomain) : LoginState()
    data class ValidationError(
        val emailError: String?,
        val passwordError: String?
    ) : LoginState()

    data class ApiError(val message: String) : LoginState()
}