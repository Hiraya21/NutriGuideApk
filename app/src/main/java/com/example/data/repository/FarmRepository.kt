package com.example.data.repository

import com.example.data.dao.FarmDao
import com.example.data.model.FarmRecord
import kotlinx.coroutines.flow.Flow

class FarmRepository(private val farmDao: FarmDao) {
    val allFarms: Flow<List<FarmRecord>> = farmDao.getAllFarms()

    suspend fun insertFarm(farm: FarmRecord) {
        farmDao.insertFarm(farm)
    }

    suspend fun deleteFarm(farm: FarmRecord) {
        farmDao.deleteFarm(farm)
    }

    suspend fun deleteFarmById(id: Int) {
        farmDao.deleteFarmById(id)
    }

    suspend fun deleteAllFarms() {
        farmDao.deleteAllFarms()
    }
}
