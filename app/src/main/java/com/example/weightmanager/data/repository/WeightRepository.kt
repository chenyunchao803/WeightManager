package com.example.weightmanager.data.repository

import com.example.weightmanager.data.dao.WeightRecordDao
import com.example.weightmanager.data.entity.WeightRecordEntity
import kotlinx.coroutines.flow.Flow

class WeightRepository(private val weightRecordDao: WeightRecordDao) {

    fun getAllRecords(): Flow<List<WeightRecordEntity>> = weightRecordDao.getAllRecords()

    fun getRecentRecords(limit: Int = 30): Flow<List<WeightRecordEntity>> =
        weightRecordDao.getRecentRecords(limit)

    fun getRecordsBetween(startDate: String, endDate: String): Flow<List<WeightRecordEntity>> =
        weightRecordDao.getRecordsBetween(startDate, endDate)

    fun searchRecords(keyword: String): Flow<List<WeightRecordEntity>> =
        weightRecordDao.searchRecords(keyword)

    suspend fun getRecordById(id: Long): WeightRecordEntity? =
        weightRecordDao.getRecordById(id)

    suspend fun getRecordByDate(date: String): WeightRecordEntity? =
        weightRecordDao.getRecordByDate(date)

    suspend fun insert(record: WeightRecordEntity): Long =
        weightRecordDao.insert(record)

    suspend fun update(record: WeightRecordEntity) =
        weightRecordDao.update(record)

    suspend fun delete(record: WeightRecordEntity) =
        weightRecordDao.delete(record)

    suspend fun deleteById(id: Long) =
        weightRecordDao.deleteById(id)

    suspend fun getRecordCount(): Int =
        weightRecordDao.getRecordCount()

    suspend fun getAverageWeight(startDate: String, endDate: String): Double? =
        weightRecordDao.getAverageWeight(startDate, endDate)

    suspend fun getMinWeight(startDate: String, endDate: String): Double? =
        weightRecordDao.getMinWeight(startDate, endDate)

    suspend fun getMaxWeight(startDate: String, endDate: String): Double? =
        weightRecordDao.getMaxWeight(startDate, endDate)
}
