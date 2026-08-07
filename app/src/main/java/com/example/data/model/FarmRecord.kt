package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farm_records")
data class FarmRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val dateFormatted: String,
    val timestamp: Long = System.currentTimeMillis(),
    val areaHectares: Double,
    val perimeterMeters: Double,
    val cropType: String,
    val pointsJson: String = "[]",
    val walkedMeters: Double = 0.0,
    val gpsAccuracy: String = "Fair",
    val boundaryPointsCount: Int = 0
)
