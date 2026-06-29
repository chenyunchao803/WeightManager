package com.example.weightmanager.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_records")
data class WeightRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "weight")
    val weight: Double,

    @ColumnInfo(name = "record_date")
    val recordDate: String, // yyyy-MM-dd

    @ColumnInfo(name = "note")
    val note: String = "",

    @ColumnInfo(name = "mood")
    val mood: Int = 0, // 0: 未记录, 1: 开心, 2: 一般, 3: 不开心

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
