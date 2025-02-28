package com.hasan.eventapp.di.modules

import com.hasan.eventapp.data.repositories.EventRepository
import com.hasan.eventapp.data.repositories.AuthRepository
import com.hasan.eventapp.data.repositories.BookingRepository
import com.hasan.eventapp.domain.usecases.auth.LoginUseCase
import com.hasan.eventapp.domain.usecases.auth.RegisterUseCase
import com.hasan.eventapp.domain.usecases.booking.CreateBookingUseCase
import com.hasan.eventapp.domain.usecases.events.GetEventDetailsUseCase
import com.hasan.eventapp.domain.usecases.events.GetEventsUseCase
import com.hasan.eventapp.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideLoginUseCase(
        authRepository: AuthRepository,
        sessionManager: SessionManager
    ): LoginUseCase {
        return LoginUseCase(authRepository, sessionManager)
    }

    @Provides
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    fun provideGetEventsUseCase(eventRepository: EventRepository): GetEventsUseCase {
        return GetEventsUseCase(eventRepository)
    }

    @Provides
    fun provideGetEventDetailsUseCase(eventRepository: EventRepository): GetEventDetailsUseCase {
        return GetEventDetailsUseCase(eventRepository)
    }

    @Provides
    fun provideCreateBookingUseCase(bookingRepository: BookingRepository): CreateBookingUseCase {
        return CreateBookingUseCase(bookingRepository)
    }
}