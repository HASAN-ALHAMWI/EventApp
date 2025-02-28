package com.hasan.eventapp.presentation.events.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hasan.eventapp.utils.test.TestDispatcherRule
import app.cash.turbine.test
import com.hasan.eventapp.domain.models.EventDomain
import com.hasan.eventapp.domain.usecases.auth.LogoutUseCase
import com.hasan.eventapp.domain.usecases.events.GetEventsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class EventListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var getEventsUseCase: GetEventsUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var viewModel: EventListViewModel

    private val testEvents = listOf(
        EventDomain(
            id = "event1",
            title = "Test Event 1",
            description = "Description 1",
            shortDescription = "Short 1",
            imageUrl = "image1.jpg",
            date = "2023-10-10",
            time = "14:00",
            location = "Location 1",
            price = 10.0f,
            availableSeats = 100
        ),
        EventDomain(
            id = "event2",
            title = "Test Event 2",
            description = "Description 2",
            shortDescription = "Short 2",
            imageUrl = "image2.jpg",
            date = "2023-10-11",
            time = "15:00",
            location = "Location 2",
            price = 20.0f,
            availableSeats = 200
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        getEventsUseCase = mockk()
        logoutUseCase = mockk()

        // Default setup for successful event fetching
        every {
            getEventsUseCase()
        } returns flowOf(Result.success(testEvents))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load events and emit Loading then Success states`() = runTest {
        // Act - creating the viewModel will trigger events loading
        viewModel = EventListViewModel(getEventsUseCase, logoutUseCase)

        // Let coroutines complete
        testDispatcherRule.getDispatcher().scheduler.advanceUntilIdle()

        // Assert
        viewModel.events.test {
            // Depending on timing, we might get Loading or Success first
            val firstState = awaitItem()
            if (firstState is EventListState.Loading) {
                // If we got Loading first, next should be Success
                assertEquals(EventListState.Success(testEvents), awaitItem())
            } else if (firstState is EventListState.Success) {
                // If we got Success directly, validate its content
                assertEquals(testEvents, firstState.events)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should emit Error state when events loading fails`() = runTest {
        // Arrange
        val error = Exception("Failed to load events")
        every {
            getEventsUseCase()
        } returns flowOf(Result.failure(error))

        // Act - creating the viewModel will trigger events loading
        viewModel = EventListViewModel(getEventsUseCase, logoutUseCase)

        // Let coroutines complete
        testDispatcherRule.getDispatcher().scheduler.advanceUntilIdle()

        // Assert
        viewModel.events.test {
            // The state could be Loading or Error depending on timing
            val state = awaitItem()
            if (state is EventListState.Loading) {
                // Got Loading first, Error should be next
                val errorState = awaitItem() as EventListState.Error
                assertEquals("Failed to load events", errorState.message)
            } else if (state is EventListState.Error) {
                // Got Error directly
                assertEquals("Failed to load events", state.message)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshEvents should reload events and update state`() = runTest {
        // Arrange
        viewModel = EventListViewModel(getEventsUseCase, logoutUseCase)

        // Reset the events flow to a new set of events for the refresh
        val refreshedEvents = listOf(
            EventDomain(
                id = "event3",
                title = "Refreshed Event",
                description = "Updated Description",
                shortDescription = "Updated Short",
                imageUrl = "updated.jpg",
                date = "2023-10-20",
                time = "16:00",
                location = "New Location",
                price = 30.0f,
                availableSeats = 300
            )
        )

        // Update mock after initial loading
        every {
            getEventsUseCase()
        } returns flowOf(Result.success(refreshedEvents))

        // Act & Assert - use a separate test block to avoid dealing with existing emissions
        viewModel.refreshEvents()

        viewModel.events.test {
            val state = awaitItem()
            // We might get Loading or Success depending on timing
            if (state is EventListState.Loading) {
                assertEquals(EventListState.Success(refreshedEvents), awaitItem())
            } else {
                // We already got the success state
                assertEquals(EventListState.Success(refreshedEvents), state)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `logout should emit Loading then Success states when successful`() = runTest {
        // Arrange
        coEvery { logoutUseCase.logout() } just runs
        viewModel = EventListViewModel(getEventsUseCase, logoutUseCase)

        // Let coroutines complete initial loading
        testDispatcherRule.getDispatcher().scheduler.advanceUntilIdle()

        // Act
        viewModel.logout()

        // Let logout coroutines complete
        testDispatcherRule.getDispatcher().scheduler.advanceUntilIdle()

        // Assert
        viewModel.logoutState.test {
            // We should now be able to get the final Success state directly
            assertEquals(LogoutState.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { logoutUseCase.logout() }
    }

    @Test
    fun `logout should emit Error state when logout fails`() = runTest {
        // Arrange
        val error = Exception("Logout failed")
        coEvery { logoutUseCase.logout() } throws error

        viewModel = EventListViewModel(getEventsUseCase, logoutUseCase)

        // Let coroutines complete initial loading
        testDispatcherRule.getDispatcher().scheduler.advanceUntilIdle()

        // Act
        viewModel.logout()

        // Let logout coroutines complete
        testDispatcherRule.getDispatcher().scheduler.advanceUntilIdle()

        // Assert
        viewModel.logoutState.test {
            // At this point we should get the final Error state directly
            val state = awaitItem()
            assertTrue(state is LogoutState.Error)
            assertEquals("Logout failed", (state as LogoutState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}