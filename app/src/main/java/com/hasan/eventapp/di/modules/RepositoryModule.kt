package com.hasan.eventapp.di.modules

import com.hasan.eventapp.data.local.dao.EventDao
import com.hasan.eventapp.data.remote.MockApiService
import com.hasan.eventapp.data.repositories.AuthRepository
import com.hasan.eventapp.data.repositories.BookingRepository
import com.hasan.eventapp.data.repositories.EventRepository
import com.hasan.eventapp.data.repositories.PaymentRepository
import com.hasan.eventapp.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideEventRepository(
        apiService: MockApiService,
        eventDao: EventDao,
        networkUtils: NetworkUtils
    ): EventRepository {
        return EventRepository(apiService, eventDao, networkUtils)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: MockApiService,
        networkUtils: NetworkUtils
    ): AuthRepository {
        return AuthRepository(apiService, networkUtils)
    }


    @Provides
    @Singleton
    fun providePaymentRepository(
        apiService: MockApiService,
        networkUtils: NetworkUtils
    ): PaymentRepository {
        return PaymentRepository(apiService, networkUtils)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(
        apiService: MockApiService,
        networkUtils: NetworkUtils
    ): BookingRepository {
        return BookingRepository(apiService, networkUtils)
    }

}