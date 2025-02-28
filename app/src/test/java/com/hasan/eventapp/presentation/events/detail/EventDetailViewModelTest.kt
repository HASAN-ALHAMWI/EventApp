package com.hasan.eventapp.presentation.events.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.hasan.eventapp.domain.models.EventDomain
import com.hasan.eventapp.domain.usecases.events.GetEventDetailsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class EventDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var getEventDetailsUseCase: GetEventDetailsUseCase
    private lateinit var viewModel: EventDetailViewModel

    private val testEvent = EventDomain(
        id = "event-id",
        title = "Test Event",
        description = "Test Description",
        shortDescription = "Short Description",
        imageUrl = "image.jpg",
        date = "2023-10-15",
        time = "15:00",
        location = "Test Location",
        price = 99.99f,
        availableSeats = 100
    )

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        getEventDetailsUseCase = mockk()
        viewModel = EventDetailViewModel(getEventDetailsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadEventDetails should emit Loading then Success states when successful`() = runTest {
        // Arrange
        every {
            getEventDetailsUseCase.invoke("event-id")
        } returns flowOf(Result.success(testEvent))

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(EventDetailUiState.Loading, awaitItem())

            viewModel.loadEventDetails("event-id")

            assertEquals(EventDetailUiState.Success(testEvent), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadEventDetails should emit Loading then Error states when failed`() = runTest {
        // Arrange
        val error = Exception("Failed to load event details")
        every {
            getEventDetailsUseCase.invoke("event-id")
        } returns flowOf(Result.failure(error))

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(EventDetailUiState.Loading, awaitItem())

            viewModel.loadEventDetails("event-id")

            val errorState = awaitItem() as EventDetailUiState.Error
            assertEquals("Failed to load event details", errorState.message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadEventDetails should handle exceptions in flow`() = runTest {
        // Arrange
        every {
            getEventDetailsUseCase.invoke("event-id")
        } returns flow { throw Exception("Flow exception") }

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(EventDetailUiState.Loading, awaitItem())

            viewModel.loadEventDetails("event-id")

            val errorState = awaitItem() as EventDetailUiState.Error
            assertEquals("Flow exception", errorState.message)

            cancelAndIgnoreRemainingEvents()
        }
    }
}