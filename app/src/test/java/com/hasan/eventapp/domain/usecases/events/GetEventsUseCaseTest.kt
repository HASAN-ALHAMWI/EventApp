package com.hasan.eventapp.domain.usecases.events

import app.cash.turbine.test
import com.hasan.eventapp.data.repositories.EventRepository
import com.hasan.eventapp.domain.models.EventDomain
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class GetEventsUseCaseTest {

    private lateinit var eventRepository: EventRepository
    private lateinit var getEventsUseCase: GetEventsUseCase

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

    private val expectedDomainEvents = testEvents

    @Before
    fun setup() {
        eventRepository = mockk()
        getEventsUseCase = GetEventsUseCase(eventRepository)
    }

    @Test
    fun `invoke should return mapped domain events on success`() = runTest {
        // Arrange
        every { eventRepository.getEvents() } returns flowOf(Result.success(testEvents))

        // Act & Assert
        getEventsUseCase().test {
            val result = awaitItem()
            assert(result.isSuccess)
            assertEquals(expectedDomainEvents, result.getOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should propagate errors`() = runTest {
        // Arrange
        val error = Exception("Failed to fetch events")
        every { eventRepository.getEvents() } returns flowOf(Result.failure(error))

        // Act & Assert
        getEventsUseCase().test {
            val result = awaitItem()
            assert(result.isFailure)
            assertEquals(error, result.exceptionOrNull())
            awaitComplete()
        }
    }
}