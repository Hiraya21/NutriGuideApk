package com.example.domain.models

data class FertilizerItem(
    val id: String,
    val name: String,
    val defaultPrice: Double,
    var bagsPerHectare: Double,
    var isSelected: Boolean = false,
    var customPrice: Double = defaultPrice,
    var isAvailable: Boolean = true,
    var nPercent: Double = 0.0,
    var pPercent: Double = 0.0,
    var kPercent: Double = 0.0
)

