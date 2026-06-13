package com.ryuzora.dangani.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ryuzora.dangani.data.local.dao.FavoriteDao
import com.ryuzora.dangani.data.local.dao.NotificationDao
import com.ryuzora.dangani.data.local.dao.ReviewDao
import com.ryuzora.dangani.data.local.dao.SearchHistoryDao
import com.ryuzora.dangani.data.local.dao.TaskApplicationDao
import com.ryuzora.dangani.data.local.dao.TaskDao
import com.ryuzora.dangani.data.local.dao.UserDao
import com.ryuzora.dangani.data.local.entity.FavoriteEntity
import com.ryuzora.dangani.data.local.entity.NotificationEntity
import com.ryuzora.dangani.data.local.entity.ReviewEntity
import com.ryuzora.dangani.data.local.entity.SearchHistoryEntity
import com.ryuzora.dangani.data.local.entity.TaskApplicationEntity
import com.ryuzora.dangani.data.local.entity.TaskEntity
import com.ryuzora.dangani.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        TaskApplicationEntity::class,
        NotificationEntity::class,
        ReviewEntity::class,
        SearchHistoryEntity::class,
        FavoriteEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DanganiDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun taskApplicationDao(): TaskApplicationDao
    abstract fun notificationDao(): NotificationDao
    abstract fun reviewDao(): ReviewDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: DanganiDatabase? = null

        fun getInstance(context: Context): DanganiDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DanganiDatabase::class.java,
                    "dangani_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

