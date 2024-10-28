package com.ihsanfrr.ourdicodingevent.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ihsanfrr.ourdicodingevent.data.local.entity.DicodingEventEntity

@Database(entities = [DicodingEventEntity::class], version = 1, exportSchema = false)
abstract class DicodingEventDatabase : RoomDatabase() {
    abstract fun eventDao(): DicodingEventDao

    companion object {
        @Volatile
        private var instance: DicodingEventDatabase? = null
        fun getInstance(context: Context): DicodingEventDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DicodingEventDatabase::class.java,
                    "ourDicoding.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
    }
}