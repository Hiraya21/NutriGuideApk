package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FarmRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {
    @Query("SELECT * FROM farm_records ORDER BY timestamp DESC")
    fun getAllFarms(): Flow<List<FarmRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarm(farm: FarmRecord)

    @Delete
    suspend fun deleteFarm(farm: FarmRecord)

    @Query("DELETE FROM farm_records WHERE id = :id")
    suspend fun deleteFarmById(id: Int)

    @Query("DELETE FROM farm_records")
    suspend fun deleteAllFarms()
}
