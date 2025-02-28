package com.hasan.eventapp.di.modules

import android.content.Context
import androidx.room.Room
import com.hasan.eventapp.data.local.EventDatabase
import com.hasan.eventapp.data.local.dao.EventDao
import com.hasan.eventapp.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EventDatabase {
        return Room.databaseBuilder(
            context,
            EventDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideEventDao(database: EventDatabase): EventDao = database.eventDao()

}