package com.hasan.eventapp.data.repositories

import com.hasan.eventapp.data.local.dao.EventDao
import com.hasan.eventapp.data.remote.MockApiService
import com.hasan.eventapp.domain.models.EventDomain
import com.hasan.eventapp.domain.repositories.IEventRepository
import com.hasan.eventapp.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val apiService: MockApiService,
    private val eventDao: EventDao,
    private val networkUtils: NetworkUtils
) : IEventRepository {
    override fun getEvents(): Flow<Result<List<EventDomain>>> = flow {
        if (networkUtils.isNetworkAvailable()) {
            try {
                val remoteEvents = apiService.getEvents()
                remoteEvents.getOrNull()?.let { events ->

                    // Insert all events to local database
                    eventDao.insertAllEvents(events)

                    emit(Result.success(events.map { it.toDomain() }))
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        } else {
            // If offline, fetch paginated events from local database
            val localEvents = eventDao.getAllEvents()
            emit(Result.success(localEvents.map { it.toDomain() }))
        }
    }

    override fun getEventDetails(eventId: String): Flow<Result<EventDomain>> = flow {
        // Try to fetch fresh data from network if available
        if (networkUtils.isNetworkAvailable()) {
            try {
                // Fetch fresh data from API
                val remoteResult = apiService.getEventDetails(eventId)
                remoteResult.getOrNull()?.let {
                    eventDao.insertEvent(it)
                    emit(Result.success(it.toDomain()))
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        } else {
            // If offline, fetch from local database
            val localEvent = eventDao.getEventById(eventId)
            localEvent?.let {
                emit(Result.success(it.toDomain()))
            }
        }
    }
}