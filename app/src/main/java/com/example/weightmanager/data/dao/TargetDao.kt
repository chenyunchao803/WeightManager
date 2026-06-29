package com.example.weightmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weightmanager.data.entity.TargetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetDao {

    @Query("SELECT * FROM targets WHERE is_active = 1 LIMIT 1")
    fun getActiveTarget(): Flow<TargetEntity?>

    @Query("SELECT * FROM targets ORDER BY created_at DESC")
    fun getAllTargets(): Flow<List<TargetEntity>>

    @Query("SELECT * FROM targets WHERE id = :id")
    suspend fun getTargetById(id: Long): TargetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(target: TargetEntity): Long

    @Update
    suspend fun update(target: TargetEntity)

    @Delete
    suspend fun delete(target: TargetEntity)

    @Query("UPDATE targets SET is_active = 0")
    suspend fun deactivateAll()

    @Query("UPDATE targets SET is_active = 1 WHERE id = :id")
    suspend fun activateTarget(id: Long)
}
