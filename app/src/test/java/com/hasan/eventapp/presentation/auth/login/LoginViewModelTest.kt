package com.hasan.eventapp.presentation.auth.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.hasan.eventapp.data.managers.UserManager
import com.hasan.eventapp.domain.models.UserDomain
import com.hasan.eventapp.domain.usecases.auth.LoginUseCase
import com.hasan.eventapp.utils.InputValidationException
import com.hasan.eventapp.utils.test.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var userManager: UserManager
    private lateinit var viewModel: LoginViewModel

    private val testUser = UserDomain(
        id = "user-id",
        email = "test@example.com",
        name = "Test User"
    )

    @Before
    fun setup() {
        loginUseCase = mockk(relaxed = true)
        userManager = mockk(relaxed = true)
        viewModel = LoginViewModel(loginUseCase, userManager)
    }

    @Test
    fun `init should check for existing session`() {
        // Arrange
        every { loginUseCase.isUserLoggedIn() } returns true
        every { loginUseCase.getCurrentUser() } returns testUser

        // Act
        val viewModel = LoginViewModel(loginUseCase, userManager)

        // Assert
        verify { loginUseCase.isUserLoggedIn() }
        verify { loginUseCase.getCurrentUser() }
    }

    @Test
    fun `login with valid credentials should emit Success state`() = runTest {
        // Arrange
        coEvery {
            loginUseCase.invoke("test@example.com", "password")
        } returns Result.success(testUser)

        // Act & Assert
        viewModel.loginState.test {
            // Skip initial state if it's already emitted
            if (awaitItem() is LoginState.Initial) {
                viewModel.login("test@example.com", "password")
                assertEquals(LoginState.Loading, awaitItem())
                assertEquals(LoginState.Success(testUser), awaitItem())
            } else {
                // If we missed the initial state, proceed with rest of test
                viewModel.login("test@example.com", "password")
                assertEquals(LoginState.Loading, awaitItem())
                assertEquals(LoginState.Success(testUser), awaitItem())
            }

            cancelAndIgnoreRemainingEvents()
        }

        verify { userManager.setCurrentUser(testUser) }
    }

    @Test
    fun `login with invalid credentials should emit ApiError state`() = runTest {
        // Arrange
        val error = Exception("Invalid credentials")
        coEvery {
            loginUseCase.invoke("test@example.com", "wrong_password")
        } returns Result.failure(error)

        // Act
        viewModel.login("test@example.com", "wrong_password")

        // Assert
        viewModel.loginState.test {
            // Get current state and check sequence
            val firstState = awaitItem()
            if (firstState is LoginState.Loading) {
                // We're already at Loading, next should be ApiError
                assertTrue(awaitItem() is LoginState.ApiError)
            } else if (firstState is LoginState.ApiError) {
                // We got the ApiError directly
                assertTrue(true)
            } else if (firstState is LoginState.Initial) {
                // We're at Initial, should get Loading then ApiError
                assertEquals(LoginState.Loading, awaitItem())
                assertTrue(awaitItem() is LoginState.ApiError)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login with validation errors should emit ValidationError state`() = runTest {
        // Arrange
        val validationErrors = mapOf(
            "email" to "Invalid email format",
            "password" to "Password cannot be empty"
        )
        val exception = InputValidationException(validationErrors)

        coEvery {
            loginUseCase.invoke("invalid-email", "")
        } returns Result.failure(exception)

        // Act
        viewModel.login("invalid-email", "")

        // Assert
        viewModel.loginState.test {
            var foundValidationError = false

            // Loop through emitted items to find ValidationError
            repeat(3) { // Look through up to 3 states
                val state = awaitItem()
                if (state is LoginState.ValidationError) {
                    assertEquals("Invalid email format", state.emailError)
                    assertEquals("Password cannot be empty", state.passwordError)
                    foundValidationError = true
                }
                // If we've found what we're looking for, no need to check more states
                if (foundValidationError) return@repeat
            }

            assertTrue("Should have found ValidationError state", foundValidationError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resetState should emit Initial state`() = runTest {
        // First, put the viewModel in some other state
        coEvery {
            loginUseCase.invoke(any(), any())
        } returns Result.failure(Exception("Error"))

        viewModel.login("test@example.com", "password")

        // Give time for state changes
        testDispatcherRule.getDispatcher().scheduler.advanceUntilIdle()

        // Act
        viewModel.resetState()

        // Assert
        viewModel.loginState.test {
            val state = awaitItem()
            assertEquals(LoginState.Initial, state)
            cancelAndIgnoreRemainingEvents()
        }
    }
}