package com.hasan.eventapp.domain.usecases.auth

import com.hasan.eventapp.data.models.User
import com.hasan.eventapp.data.repositories.AuthRepository
import com.hasan.eventapp.domain.models.UserDomain
import com.hasan.eventapp.utils.InputValidationException
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RegisterUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var registerUseCase: RegisterUseCase


    private val testUserDomain = UserDomain(
        id = "test-id",
        email = "test@example.com",
        name = "Test User"
    )

    @Before
    fun setup() {
        authRepository = mockk(relaxed = true)
        registerUseCase = RegisterUseCase(authRepository)
    }

    @Test
    fun `invoke with valid data should register user successfully`() = runTest {
        // Arrange
        val userSlot = slot<User>()
        coEvery {
            authRepository.register(capture(userSlot))
        } returns Result.success(testUserDomain)

        // Act
        val result = registerUseCase(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123",
            name = "Test User"
        )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(testUserDomain, result.getOrNull())

        // Verify the captured user has the correct data
        val capturedUser = userSlot.captured
        assertEquals("test@example.com", capturedUser.email)
        assertEquals("password123", capturedUser.password)
        assertEquals("Test User", capturedUser.name)
    }

    @Test
    fun `invoke with mismatched passwords should return validation error`() = runTest {
        // Act
        val result = registerUseCase(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "different_password",
            name = "Test User"
        )

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InputValidationException)
        val exception = result.exceptionOrNull() as InputValidationException
        assertEquals("Passwords do not match", exception.getError("confirmPassword"))
    }

    @Test
    fun `invoke with invalid email should return validation error`() = runTest {
        // Act
        val result = registerUseCase(
            email = "invalid-email",
            password = "password123",
            confirmPassword = "password123",
            name = "Test User"
        )

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InputValidationException)
        val exception = result.exceptionOrNull() as InputValidationException
        assertEquals("Invalid email format", exception.getError("email"))
    }

    @Test
    fun `invoke with empty name should return validation error`() = runTest {
        // Act
        val result = registerUseCase(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123",
            name = ""
        )

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InputValidationException)
        val exception = result.exceptionOrNull() as InputValidationException
        assertEquals("Full name cannot be empty", exception.getError("name"))
    }

    @Test
    fun `invoke with short password should return validation error`() = runTest {
        // Act
        val result = registerUseCase(
            email = "test@example.com",
            password = "pass",
            confirmPassword = "pass",
            name = "Test User"
        )

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InputValidationException)
        val exception = result.exceptionOrNull() as InputValidationException
        assertEquals("Password must be between 6-20 characters", exception.getError("password"))
    }

    @Test
    fun `invoke with registration failure should propagate error`() = runTest {
        // Arrange
        val error = Exception("User already exists")
        coEvery {
            authRepository.register(any())
        } returns Result.failure(error)

        // Act
        val result = registerUseCase(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123",
            name = "Test User"
        )

        // Assert
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}