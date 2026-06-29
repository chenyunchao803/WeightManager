package com.example.weightmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weightmanager.data.entity.WeightRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightRecordDao {

    @Query("SELECT * FROM weight_records ORDER BY record_date DESC")
    fun getAllRecords(): Flow<List<WeightRecordEntity>>

    @Query("SELECT * FROM weight_records WHERE id = :id")
    suspend fun getRecordById(id: Long): WeightRecordEntity?

    @Query("SELECT * FROM weight_records WHERE record_date = :date LIMIT 1")
    suspend fun getRecordByDate(date: String): WeightRecordEntity?

    @Query("SELECT * FROM weight_records WHERE record_date BETWEEN :startDate AND :endDate ORDER BY record_date ASC")
    fun getRecordsBetween(startDate: String, endDate: String): Flow<List<WeightRecordEntity>>

    @Query("SELECT * FROM weight_records WHERE note LIKE '%' || :keyword || '%' ORDER BY record_date DESC")
    fun searchRecords(keyword: String): Flow<List<WeightRecordEntity>>

    @Query("SELECT * FROM weight_records ORDER BY record_date DESC LIMIT :limit")
    fun getRecentRecords(limit: Int): Flow<List<WeightRecordEntity>>

    @Query("SELECT COUNT(*) FROM weight_records")
    suspend fun getRecordCount(): Int

    @Query("SELECT AVG(weight) FROM weight_records WHERE record_date BETWEEN :startDate AND :endDate")
    suspend fun getAverageWeight(startDate: String, endDate: String): Double?

    @Query("SELECT MIN(weight) FROM weight_records WHERE record_date BETWEEN :startDate AND :endDate")
    suspend fun getMinWeight(startDate: String, endDate: String): Double?

    @Query("SELECT MAX(weight) FROM weight_records WHERE record_date BETWEEN :startDate AND :endDate")
    suspend fun getMaxWeight(startDate: String, endDate: String): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: WeightRecordEntity): Long

    @Update
    suspend fun update(record: WeightRecordEntity)

    @Delete
    suspend fun delete(record: WeightRecordEntity)

    @Query("DELETE FROM weight_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
