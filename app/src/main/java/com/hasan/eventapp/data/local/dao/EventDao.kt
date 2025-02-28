package com.hasan.eventapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hasan.eventapp.data.models.Event

@Dao
interface EventDao {
    @Query("SELECT * FROM EVENT")
    suspend fun getAllEvents(): List<Event>

    @Query("SELECT * FROM EVENT WHERE id = :id")
    suspend fun getEventById(id: String): Event?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEvents(events: List<Event>)

    @Delete
    suspend fun deleteEvent(event: Event)
}