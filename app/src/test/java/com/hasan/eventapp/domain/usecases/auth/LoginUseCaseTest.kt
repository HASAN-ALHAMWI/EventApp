package com.hasan.eventapp.domain.usecases.auth

import com.hasan.eventapp.data.models.User
import com.hasan.eventapp.data.repositories.AuthRepository
import com.hasan.eventapp.domain.models.UserDomain
import com.hasan.eventapp.domain.repositories.IAuthRepository
import com.hasan.eventapp.utils.InputValidationException
import com.hasan.eventapp.utils.SessionManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginUseCaseTest {

    // Change this to use the interface instead of implementation
    private lateinit var authRepository: IAuthRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var loginUseCase: LoginUseCase

    private val testUserDomain = UserDomain(
        id = "test-id",
        email = "test@example.com",
        name = "Test User"
    )

    @Before
    fun setup() {
        // Mock the interface instead of the implementation
        authRepository = mockk<IAuthRepository>(relaxed = true)
        sessionManager = mockk(relaxed = true)
        loginUseCase = LoginUseCase(authRepository, sessionManager)
    }

    @Test
    fun `invoke with valid credentials should return success`() = runTest {
        // Arrange - Now mocking the interface method which returns domain model directly
        coEvery {
            authRepository.login("user1@gmail.com", "Test@123")
        } returns Result.success(testUserDomain) // Note: Returns domain model, not data model

        // Act
        val result = loginUseCase("user1@gmail.com", "Test@123")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(testUserDomain, result.getOrNull())
        verify { sessionManager.saveUserSession(testUserDomain) }
    }

    // Other tests also need similar updates to use domain models in expectations
    @Test
    fun `invoke with invalid credentials should return failure from repository`() = runTest {
        // Arrange
        val error = Exception("Invalid credentials")
        coEvery {
            authRepository.login("test@example.com", "wrong_password")
        } returns Result.failure(error)

        // Act
        val result = loginUseCase("test@example.com", "wrong_password")

        // Assert
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `invoke with invalid email format should return validation error`() = runTest {
        // Act
        val result = loginUseCase("invalid-email", "password123")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InputValidationException)
    }

    @Test
    fun `invoke with empty password should return validation error`() = runTest {
        // Act
        val result = loginUseCase("test@example.com", "")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InputValidationException)
    }

    @Test
    fun `isUserLoggedIn should delegate to sessionManager`() {
        // Arrange
        every { sessionManager.isLoggedIn() } returns true

        // Act
        val result = loginUseCase.isUserLoggedIn()

        // Assert
        assertTrue(result)
        verify { sessionManager.isLoggedIn() }
    }

    @Test
    fun `getCurrentUser should delegate to sessionManager`() {
        // Arrange
        every { sessionManager.getCurrentUser() } returns testUserDomain

        // Act
        val result = loginUseCase.getCurrentUser()

        // Assert
        assertEquals(testUserDomain, result)
        verify { sessionManager.getCurrentUser() }
    }
}