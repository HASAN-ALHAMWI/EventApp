package com.hasan.eventapp.di.modules

import android.content.Context
import com.hasan.eventapp.data.remote.MockApiService
import com.hasan.eventapp.data.remote.MockApiServiceImpl
import com.hasan.eventapp.data.managers.MockDataManager
import com.hasan.eventapp.data.managers.UserManager
import com.hasan.eventapp.utils.NetworkUtils
import com.hasan.eventapp.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideNetworkUtils(@ApplicationContext context: Context): NetworkUtils {
        return NetworkUtils(context)
    }

    @Provides
    @Singleton
    fun provideMockApiService(mockDataManager: MockDataManager): MockApiService {
        return MockApiServiceImpl(mockDataManager)
    }

    @Provides
    @Singleton
    fun provideMockDataManager(@ApplicationContext context: Context): MockDataManager {
        return MockDataManager(context)
    }

    @Provides
    @Singleton
    fun provideUserManager(@ApplicationContext context: Context): UserManager {
        return UserManager(context)
    }
}