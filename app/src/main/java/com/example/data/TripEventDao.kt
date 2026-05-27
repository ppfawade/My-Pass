package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripEventDao {
    @Query("SELECT * FROM trip_events WHERE tripId = :tripId")
    fun getEventsForTrip(tripId: Int): Flow<List<TripEvent>>
    
    @Query("SELECT * FROM trip_events WHERE tripId = :tripId AND status = :status")
    fun getEventsForTripByStatus(tripId: Int, status: EventStatus): Flow<List<TripEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: TripEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<TripEvent>)

    @Query("SELECT COUNT(*) FROM trip_events")
    suspend fun getCount(): Int

    @androidx.room.Delete
    suspend fun deleteEvent(event: TripEvent)

    @Query("DELETE FROM trip_events")
    suspend fun clearAll()
}
