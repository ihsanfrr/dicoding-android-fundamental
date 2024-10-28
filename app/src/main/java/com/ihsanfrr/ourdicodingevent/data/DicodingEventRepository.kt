package com.ihsanfrr.ourdicodingevent.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.ihsanfrr.ourdicodingevent.data.local.entity.DicodingEventEntity
import com.ihsanfrr.ourdicodingevent.data.local.room.DicodingEventDao
import com.ihsanfrr.ourdicodingevent.data.remote.retrofit.ApiService

class DicodingEventRepository private constructor (
    private val apiService: ApiService,
    private val eventDao: DicodingEventDao,
) {

    fun getEvents(isActive: Int): LiveData<Result<List<DicodingEventEntity>>> = liveData {
        emit(Result.Loading)

        try {
            val localData = if(isActive == 1) eventDao.getActiveEvents() else eventDao.getInactiveEvents()
            val response = apiService.getEvents(isActive)
            val events = response.listEvents

            if (localData.isNotEmpty() && localData.size == events.size && localData == events) {
                emit(Result.Success(localData))
            } else {
                try {
                    val eventList = events.map { event ->
                        val isFavorite = eventDao.isFavorites(event.id.toString())
                        DicodingEventEntity(
                            event.id.toString(),
                            event.name,
                            event.summary,
                            event.description,
                            event.imageLogo,
                            event.mediaCover,
                            event.category,
                            event.ownerName,
                            event.cityName,
                            event.quota,
                            event.registrants,
                            event.beginTime,
                            event.endTime,
                            event.link,
                            isFavorite,
                            isActive = true
                        )
                    }
                    eventDao.deleteActiveEvents()
                    eventDao.insertEvent(eventList)
                    emit(Result.Success(eventList))
                } catch (e: Exception) {
                    emit(Result.Error("No internet connection and no local data available"))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    fun searchEvent(active: Int, query: String): LiveData<Result<List<DicodingEventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val localEvent = eventDao.searchEvents(active, query)

            emitSource(localEvent.map { eventList ->
                if (eventList.isEmpty()) {
                    Result.Success(emptyList())
                } else {
                    Result.Success(eventList)
                }
            })
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getEvent(id: String): LiveData<Result<DicodingEventEntity>> = liveData {
        emit(Result.Loading)
        try {
            val localEvent = eventDao.getEventById(id)

            if (localEvent != null) {
                emit(Result.Success(localEvent))
            } else {
                emit(Result.Error("Dicoding Event not found"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getFavoritesEvents(): LiveData<List<DicodingEventEntity>> {
        return eventDao.getFavoritesEvents()
    }

    suspend fun setFavoritesEvents(event: DicodingEventEntity, favoriteState: Boolean) {
        event.isFavorite = favoriteState
        eventDao.updateEvent(event)
    }

    suspend fun getNearestActiveEvent(): DicodingEventEntity? {
        val currentTime = System.currentTimeMillis()
        val nearestEvent = eventDao.getNearestActiveEvents(currentTime)
        return nearestEvent
    }

    companion object {
        @Volatile
        private var instance: DicodingEventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: DicodingEventDao,
        ): DicodingEventRepository =
            instance ?: synchronized(this) {
                instance ?: DicodingEventRepository(apiService, eventDao)
            }.also {
                instance = it
            }
    }
}