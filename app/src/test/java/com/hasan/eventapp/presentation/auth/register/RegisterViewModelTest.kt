package com.hasan.eventapp.presentation.auth.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hasan.eventapp.domain.models.UserDomain
import com.hasan.eventapp.domain.usecases.auth.RegisterUseCase
import com.hasan.eventapp.utils.InputValidationException
import com.hasan.eventapp.utils.test.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class RegisterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var viewModel: RegisterViewModel

    private val testUser = UserDomain(
        id = "user-id",
        email = "test@example.com",
        name = "Test User"
    )

    @Before
    fun setup() {
        registerUseCase = mockk()
        viewModel = RegisterViewModel(registerUseCase)
    }

    @Test
    fun `register with validation errors should emit ValidationError state`() = runTest {
        // Create validation errors with map
        val errorMap = mapOf(
            "email" to "Invalid email format",
            "password" to "Password must be between 6-20 characters",
            "confirmPassword" to "Passwords do not match",
            "name" to "Full name cannot be empty"
        )

        // Create mock to return errors
        val exception = InputValidationException(errorMap)
        coEvery {
            registerUseCase.invoke(
                email = "invalid-email",
                password = "pass",
                confirmPassword = "password",
                name = ""
            )
        } returns Result.failure(exception)

        // Create a test ViewModel
        val testViewModel = RegisterViewModel(registerUseCase)

        // Call the register function
        testViewModel.register(
            name = "",
            email = "invalid-email",
            password = "pass",
            confirmPassword = "password"
        )

        // Wait for coroutines to complete
        advanceUntilIdle()

        // Get the current state directly through the property
        val currentState = testViewModel.registrationState.value

        // Assert the state is ValidationError with correct values
        assertTrue(
            "Current state should be ValidationError",
            currentState is RegistrationState.ValidationError
        )
        val errorState = currentState as RegistrationState.ValidationError

        assertEquals("Invalid email format", errorState.emailError)
        assertEquals("Password must be between 6-20 characters", errorState.passwordError)
        assertEquals("Passwords do not match", errorState.confirmPasswordError)
        assertEquals("Full name cannot be empty", errorState.nameError)
    }

    @Test
    fun `register with API error should emit ApiError state`() = runTest {
        // Arrange
        val error = Exception("User with this email already exists")

        coEvery {
            registerUseCase.invoke(
                email = "test@example.com",
                password = "password123",
                confirmPassword = "password123",
                name = "Test User"
            )
        } returns Result.failure(error)

        // Create a test ViewModel
        val testViewModel = RegisterViewModel(registerUseCase)

        // Call the register function
        testViewModel.register(
            name = "Test User",
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123"
        )

        // Wait for coroutines to complete
        advanceUntilIdle()

        // Get the current state directly
        val currentState = testViewModel.registrationState.value

        // Assert the state is ApiError with correct message
        assertTrue("Current state should be ApiError", currentState is RegistrationState.ApiError)
        val errorState = currentState as RegistrationState.ApiError

        assertEquals("User with this email already exists", errorState.message)
    }

    @Test
    fun `resetState should emit Initial state`() = runTest {
        // Create a fresh viewModel
        val viewModel = RegisterViewModel(registerUseCase)

        // Set up to receive an error state first
        val error = Exception("API error")
        coEvery {
            registerUseCase.invoke(any(), any(), any(), any())
        } returns Result.failure(error)

        // Get to an error state
        viewModel.register(
            name = "Test User",
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123"
        )

        // Let the error state take effect
        advanceUntilIdle()

        // Now reset state
        viewModel.resetState()

        // Check current state is Initial via direct collection
        var initialStateReceived = false

        val job = launch {
            viewModel.registrationState.collect { state ->
                if (state is RegistrationState.Initial) {
                    initialStateReceived = true
                }
            }
        }

        // Let any state changes happen
        advanceUntilIdle()

        // Clean up
        job.cancel()

        // Assert
        assertTrue("Should have received Initial state after reset", initialStateReceived)
    }
}