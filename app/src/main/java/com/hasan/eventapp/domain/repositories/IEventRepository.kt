package com.hasan.eventapp.domain.repositories

import com.hasan.eventapp.domain.models.EventDomain
import kotlinx.coroutines.flow.Flow

interface IEventRepository {
    fun getEvents(): Flow<Result<List<EventDomain>>>
    fun getEventDetails(eventId: String): Flow<Result<EventDomain>>
}