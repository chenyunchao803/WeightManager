package com.example.weightmanager.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "targets")
data class TargetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "target_weight")
    val targetWeight: Double,

    @ColumnInfo(name = "start_weight")
    val startWeight: Double,

    @ColumnInfo(name = "start_date")
    val startDate: String, // yyyy-MM-dd

    @ColumnInfo(name = "target_date")
    val targetDate: String, // yyyy-MM-dd

    @ColumnInfo(name = "daily_calorie_goal")
    val dailyCalorieGoal: Int = 2000,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
