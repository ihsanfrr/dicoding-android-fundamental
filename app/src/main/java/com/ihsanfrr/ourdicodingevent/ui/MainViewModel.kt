package com.ihsanfrr.ourdicodingevent.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import com.ihsanfrr.ourdicodingevent.data.DicodingEventRepository
import com.ihsanfrr.ourdicodingevent.data.local.entity.DicodingEventEntity
import com.ihsanfrr.ourdicodingevent.ui.notification.DailyReminderWorker
import com.ihsanfrr.ourdicodingevent.ui.setting.SettingPreferences
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val pref: SettingPreferences,
    private val dicodingEventRepository: DicodingEventRepository,
    private val workManager: androidx.work.WorkManager): ViewModel() {
    private val _reminderIsEnabled = MutableLiveData<Boolean>()
    val reminderIsEnabled: LiveData<Boolean> = _reminderIsEnabled

    init {
        viewModelScope.launch {
            pref.getReminderPreference().collect {
                _reminderIsEnabled.value = it
            }
        }
    }

    fun fetchActiveEvents(context: Context) = dicodingEventRepository.getActiveEvents(context)

    fun fetchInactiveEvents(context: Context) = dicodingEventRepository.getInactiveEvent(context)

    fun searchEvents(active: Int, query: String) = dicodingEventRepository.searchEvent(active, query)

    fun getDetailEvent(id: String) = dicodingEventRepository.getEvent(id)

    fun fetchFavoritesEvents() = dicodingEventRepository.getFavoritesEvents()

    fun updateFavoriteStatus(event: DicodingEventEntity, favoriteState: Boolean) {
        viewModelScope.launch {
            setFavoritesEvents(event, favoriteState)
        }
    }

    private suspend fun setFavoritesEvents(event: DicodingEventEntity, favoriteState: Boolean) {
        dicodingEventRepository.setFavoritesEvents(event, favoriteState)
    }
    fun getThemeSettings() : LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun toggleReminder(enabled: Boolean) {
        viewModelScope.launch {
            pref.saveReminderPreference(enabled)
            _reminderIsEnabled.value = enabled
            if (enabled) {
                scheduleReminder()
            } else {
                cancelReminder()
            }
        }
    }

    private fun scheduleReminder() {
        val reminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderRequest
        )
    }

    private fun cancelReminder() {
        workManager.cancelUniqueWork(REMINDER_WORK_NAME)
    }

    companion object {
        private const val REMINDER_WORK_NAME = "daily_event_reminder"
    }

}