package com.hasan.eventapp.data.repositories

import com.hasan.eventapp.data.models.User
import com.hasan.eventapp.data.remote.MockApiService
import com.hasan.eventapp.domain.models.UserDomain
import com.hasan.eventapp.domain.repositories.IAuthRepository
import com.hasan.eventapp.utils.NetworkUtils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthRepositoryTest {

    private lateinit var apiService: MockApiService
    private lateinit var networkUtils: NetworkUtils
    private lateinit var authRepository: IAuthRepository

    private val testUser = User(
        id = "user-id",
        email = "test@example.com",
        password = "password",
        name = "Test User"
    )

    private val testUserDomain = UserDomain(
        id = "user-id",
        email = "test@example.com",
        name = "Test User"
    )

    @Before
    fun setup() {
        apiService = mockk()
        networkUtils = mockk()
        authRepository = AuthRepository(apiService, networkUtils)
    }

    @Test
    fun `login should return user when network is available and credentials are valid`() = runTest {
        // Arrange
        every { networkUtils.isNetworkAvailable() } returns true
        coEvery {
            apiService.login("test@example.com", "password")
        } returns Result.success(testUser)

        // Act
        val result = authRepository.login("test@example.com", "password")

        // Assert - compare with domain model
        assertTrue(result.isSuccess)
        assertEquals(testUserDomain, result.getOrNull())
        coVerify {
            apiService.login("test@example.com", "password")
        }
    }

    @Test
    fun `login should return error when network is unavailable`() = runTest {
        // Arrange
        every { networkUtils.isNetworkAvailable() } returns false

        // Act
        val result = authRepository.login("test@example.com", "password")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull()?.message?.contains("Internet connection required") ?: false
        )
    }

    @Test
    fun `login should propagate API errors`() = runTest {
        // Arrange
        val error = Exception("Invalid credentials")
        every { networkUtils.isNetworkAvailable() } returns true
        coEvery {
            apiService.login("test@example.com", "wrong_password")
        } returns Result.failure(error)

        // Act
        val result = authRepository.login("test@example.com", "wrong_password")

        // Assert
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `register should return user when network is available and registration is successful`() =
        runTest {
            // Arrange
            every { networkUtils.isNetworkAvailable() } returns true
            coEvery {
                apiService.register(any())
            } returns Result.success(testUser)

            // Act - use the new register method signature
            val result = authRepository.register(testUser)

            // Assert - compare with domain model
            assertTrue(result.isSuccess)
            assertEquals(testUserDomain, result.getOrNull())
            coVerify {
                apiService.register(any())
            }
        }

    @Test
    fun `register should return error when network is unavailable`() = runTest {
        // Arrange
        every { networkUtils.isNetworkAvailable() } returns false

        // Act - use the new register method signature
        val result = authRepository.register(testUser)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull()?.message?.contains("Internet connection required") ?: false
        )
    }

    @Test
    fun `register should propagate API errors`() = runTest {
        // Arrange
        val error = Exception("User with this email already exists")
        every { networkUtils.isNetworkAvailable() } returns true
        coEvery {
            apiService.register(any())
        } returns Result.failure(error)

        // Act - use the new register method signature
        val result = authRepository.register(testUser)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}