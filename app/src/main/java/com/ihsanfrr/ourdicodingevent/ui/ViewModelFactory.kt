package com.ihsanfrr.ourdicodingevent.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ihsanfrr.ourdicodingevent.data.DicodingEventRepository
import com.ihsanfrr.ourdicodingevent.di.Injection
import com.ihsanfrr.ourdicodingevent.ui.setting.SettingPreferences

class ViewModelFactory(
    private val pref: SettingPreferences,
    private val eventRepository: DicodingEventRepository,
    private val workManager: androidx.work.WorkManager
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(pref, eventRepository, workManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context, pref: SettingPreferences): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(pref, Injection.provideRepository(context), Injection.provideWorkManager(context))
            }.also { instance = it }
    }
}