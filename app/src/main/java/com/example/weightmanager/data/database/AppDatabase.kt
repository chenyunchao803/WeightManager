package com.example.weightmanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weightmanager.data.dao.TargetDao
import com.example.weightmanager.data.dao.WeightRecordDao
import com.example.weightmanager.data.entity.TargetEntity
import com.example.weightmanager.data.entity.WeightRecordEntity

@Database(
    entities = [
        WeightRecordEntity::class,
        TargetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weightRecordDao(): WeightRecordDao
    abstract fun targetDao(): TargetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weight_manager_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
