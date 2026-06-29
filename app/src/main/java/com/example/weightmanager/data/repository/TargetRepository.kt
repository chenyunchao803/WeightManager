package com.example.weightmanager.data.repository

import com.example.weightmanager.data.dao.TargetDao
import com.example.weightmanager.data.entity.TargetEntity
import kotlinx.coroutines.flow.Flow

class TargetRepository(private val targetDao: TargetDao) {

    fun getActiveTarget(): Flow<TargetEntity?> = targetDao.getActiveTarget()

    fun getAllTargets(): Flow<List<TargetEntity>> = targetDao.getAllTargets()

    suspend fun getTargetById(id: Long): TargetEntity? = targetDao.getTargetById(id)

    suspend fun insert(target: TargetEntity): Long = targetDao.insert(target)

    suspend fun update(target: TargetEntity) = targetDao.update(target)

    suspend fun delete(target: TargetEntity) = targetDao.delete(target)

    suspend fun setActiveTarget(id: Long) {
        targetDao.deactivateAll()
        targetDao.activateTarget(id)
    }

    suspend fun deactivateAll() = targetDao.deactivateAll()
}
