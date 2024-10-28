package com.ihsanfrr.ourdicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ihsanfrr.ourdicodingevent.data.local.entity.DicodingEventEntity

@Dao
interface DicodingEventDao {
    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: String): DicodingEventEntity?

    @Query("SELECT * FROM events WHERE isActive = :active AND name LIKE '%' || :query || '%' ORDER BY date(beginTime) ASC")
    fun searchEvents(active: Int, query: String): LiveData<List<DicodingEventEntity>>

    @Query("SELECT * FROM events WHERE isActive = 0 ORDER BY date(beginTime) DESC")
    suspend fun getInactiveEvents(): List<DicodingEventEntity>

    @Query("SELECT * FROM events WHERE isActive = 1 ORDER BY date(beginTime) ASC")
    suspend fun getActiveEvents(): List<DicodingEventEntity>

    @Query("SELECT * FROM events WHERE isFavorite = 1")
    fun getFavoritesEvents(): LiveData<List<DicodingEventEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(event: List<DicodingEventEntity>)

    @Update
    suspend fun updateEvent(event: DicodingEventEntity)

    @Query("DELETE FROM events WHERE isActive = 0")
    suspend fun deleteInactiveEvents()

    @Query("DELETE FROM events WHERE isActive = 1")
    suspend fun deleteActiveEvents()

    @Query("SELECT EXISTS(SELECT * FROM events WHERE id = :id AND isFavorite = 1)")
    suspend fun isFavorites(id: String): Boolean

    @Query("SELECT * FROM events WHERE isActive = 1 AND date(beginTime) >= :currentTime ORDER BY date(beginTime) ASC LIMIT 1")
    suspend fun getNearestActiveEvents(currentTime: Long): DicodingEventEntity?

}