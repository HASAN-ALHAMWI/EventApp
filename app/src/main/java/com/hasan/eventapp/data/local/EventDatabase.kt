package com.hasan.eventapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hasan.eventapp.data.local.dao.EventDao
import com.hasan.eventapp.data.models.Event
import com.hasan.eventapp.utils.Constants.DATABASE_NAME

@Database(entities = [Event::class], version = 1, exportSchema = true)
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}