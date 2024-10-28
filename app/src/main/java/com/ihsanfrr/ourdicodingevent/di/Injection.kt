package com.ihsanfrr.ourdicodingevent.di

import android.content.Context
import com.ihsanfrr.ourdicodingevent.data.DicodingEventRepository
import com.ihsanfrr.ourdicodingevent.data.local.room.DicodingEventDatabase
import com.ihsanfrr.ourdicodingevent.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): DicodingEventRepository {
        val apiService = ApiConfig.getApiService()
        val database = DicodingEventDatabase.getInstance(context)
        val dao = database.eventDao()
        return DicodingEventRepository.getInstance(apiService, dao)
    }
    fun provideWorkManager(context: Context): androidx.work.WorkManager {
        return androidx.work.WorkManager.getInstance(context)
    }
}