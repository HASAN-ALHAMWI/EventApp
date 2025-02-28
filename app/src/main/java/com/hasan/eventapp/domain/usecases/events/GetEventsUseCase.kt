package com.hasan.eventapp.domain.usecases.events

import com.hasan.eventapp.domain.models.EventDomain
import com.hasan.eventapp.domain.repositories.IEventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val eventRepository: IEventRepository
) {
    operator fun invoke(): Flow<Result<List<EventDomain>>> =
        eventRepository.getEvents()
}