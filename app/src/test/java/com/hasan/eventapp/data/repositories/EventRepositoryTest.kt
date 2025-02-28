package com.hasan.eventapp.data.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.hasan.eventapp.data.local.dao.EventDao
import com.hasan.eventapp.data.models.Event
import com.hasan.eventapp.data.remote.MockApiService
import com.hasan.eventapp.domain.models.EventDomain
import com.hasan.eventapp.domain.repositories.IEventRepository
import com.hasan.eventapp.utils.NetworkUtils
import com.hasan.eventapp.utils.test.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class EventRepositoryTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var apiService: MockApiService
    private lateinit var eventDao: EventDao
    private lateinit var networkUtils: NetworkUtils
    private lateinit var eventRepository: IEventRepository

    private val testEvents = listOf(
        Event(
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
        Event(
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

    private val testEventsDomain = testEvents.map {
        EventDomain(
            id = it.id,
            title = it.title,
            description = it.description,
            shortDescription = it.shortDescription,
            imageUrl = it.imageUrl,
            date = it.date,
            time = it.time,
            location = it.location,
            price = it.price,
            availableSeats = it.availableSeats
        )
    }

    @Before
    fun setup() {
        apiService = mockk()
        eventDao = mockk(relaxed = true)
        networkUtils = mockk()
        eventRepository = EventRepository(apiService, eventDao, networkUtils)
    }

    @Test
    fun `getEvents should return remote data when network is available`() = runTest {
        // Arrange
        every { networkUtils.isNetworkAvailable() } returns true
        coEvery { apiService.getEvents() } returns Result.success(testEvents)

        // Act & Assert
        eventRepository.getEvents().test {
            val result = awaitItem()
            assert(result.isSuccess)
            assertEquals(testEventsDomain, result.getOrNull())
            coVerify { eventDao.insertAllEvents(testEvents) }
            awaitComplete()
        }
    }

    @Test
    fun `getEvents should return local data when network is unavailable`() = runTest {
        // Arrange
        every { networkUtils.isNetworkAvailable() } returns false
        coEvery { eventDao.getAllEvents() } returns testEvents

        // Act & Assert
        eventRepository.getEvents().test {
            val result = awaitItem()
            assert(result.isSuccess)
            assertEquals(testEventsDomain, result.getOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `getEventDetails should return remote data when network is available`() = runTest {
        // Arrange
        val testEvent = testEvents[0]
        val testEventDomain = testEventsDomain[0]
        every { networkUtils.isNetworkAvailable() } returns true
        coEvery { apiService.getEventDetails(testEvent.id) } returns Result.success(testEvent)

        // Act & Assert
        eventRepository.getEventDetails(testEvent.id).test {
            val result = awaitItem()
            assert(result.isSuccess)
            assertEquals(testEventDomain, result.getOrNull())
            coVerify { eventDao.insertEvent(testEvent) }
            awaitComplete()
        }
    }

    @Test
    fun `getEventDetails should return local data when network is unavailable`() = runTest {
        // Arrange
        val testEvent = testEvents[0]
        val testEventDomain = testEventsDomain[0]
        every { networkUtils.isNetworkAvailable() } returns false
        coEvery { eventDao.getEventById(testEvent.id) } returns testEvent

        // Act & Assert
        eventRepository.getEventDetails(testEvent.id).test {
            val result = awaitItem()
            assert(result.isSuccess)
            assertEquals(testEventDomain, result.getOrNull())
            awaitComplete()
        }
    }
}