package com.hasan.eventapp.domain.usecases.events

import com.hasan.eventapp.domain.models.EventDomain
import com.hasan.eventapp.domain.repositories.IEventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventDetailsUseCase @Inject constructor(
    private val eventRepository: IEventRepository
) {
    operator fun invoke(eventId: String): Flow<Result<EventDomain>> =
        eventRepository.getEventDetails(eventId)
}