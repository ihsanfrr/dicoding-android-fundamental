package com.ihsanfrr.ourdicodingevent.ui.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ihsanfrr.ourdicodingevent.data.DicodingEventRepository
import com.ihsanfrr.ourdicodingevent.data.local.entity.DicodingEventEntity
import com.ihsanfrr.ourdicodingevent.data.local.room.DicodingEventDatabase
import com.ihsanfrr.ourdicodingevent.data.remote.retrofit.ApiConfig
import com.ihsanfrr.ourdicodingevent.helpers.NotificationHelper
import kotlinx.coroutines.coroutineScope

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val repository = DicodingEventRepository.getInstance(
                ApiConfig.getApiService(),
                DicodingEventDatabase.getInstance(applicationContext).eventDao()
            )
            val nearestEvent = repository.getNearestActiveEvent()

            if (nearestEvent != null) {
                showNotification(nearestEvent)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(event: DicodingEventEntity) {
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showNotification(event)
    }
}