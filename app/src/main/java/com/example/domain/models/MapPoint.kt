package com.example.domain.models

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class MapPoint(
    val lat: Double,
    val lng: Double,
    val timestamp: Long = System.currentTimeMillis()
)

object MapUtils {
    // Earth radius in meters
    private const val EARTH_RADIUS = 6371000.0

    /**
     * Calculates distance between two coordinates in meters using Haversine formula
     */
    fun calculateDistanceMeters(p1: MapPoint, p2: MapPoint): Double {
        val dLat = Math.toRadians(p2.lat - p1.lat)
        val dLng = Math.toRadians(p2.lng - p1.lng)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(p1.lat)) * cos(Math.toRadians(p2.lat)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS * c
    }

    /**
     * Calculates total perimeter distance for a list of points
     */
    fun calculateTotalDistance(points: List<MapPoint>): Double {
        if (points.size < 2) return 0.0
        var total = 0.0
        for (i in 0 until points.size - 1) {
            total += calculateDistanceMeters(points[i], points[i + 1])
        }
        return total
    }

    /**
     * Calculates polygon area in square meters using spherical Shoelace formula on Earth radius
     */
    fun calculatePolygonAreaSquareMeters(points: List<MapPoint>): Double {
        if (points.size < 3) return 0.0
        var total = 0.0
        val size = points.size
        for (i in points.indices) {
            val p1 = points[i]
            val p2 = points[(i + 1) % size]
            val tanLat = Math.toRadians(p2.lat - p1.lat)
            val meanLng = Math.toRadians(p1.lng + p2.lng) / 2.0
            total += (p2.lng - p1.lng) * Math.toRadians(6371000.0) * Math.toRadians(6371000.0) * sin(Math.toRadians((p1.lat + p2.lat) / 2.0))
        }
        
        // Secondary planar approximation for accurate localized small fields
        var areaPlanar = 0.0
        val refLat = Math.toRadians(points[0].lat)
        for (i in points.indices) {
            val j = (i + 1) % size
            val x1 = Math.toRadians(points[i].lng) * EARTH_RADIUS * cos(refLat)
            val y1 = Math.toRadians(points[i].lat) * EARTH_RADIUS
            val x2 = Math.toRadians(points[j].lng) * EARTH_RADIUS * cos(refLat)
            val y2 = Math.toRadians(points[j].lat) * EARTH_RADIUS
            areaPlanar += (x1 * y2 - x2 * y1)
        }
        return kotlin.math.abs(areaPlanar / 2.0)
    }

    fun squareMetersToHectares(sqMeters: Double): Double {
        return sqMeters / 10000.0
    }

    /**
     * Calculates destination MapPoint given a start point, distance in meters, and bearing in degrees
     */
    fun destinationPoint(start: MapPoint, distanceMeters: Double, bearingDegrees: Double): MapPoint {
        val distRatio = distanceMeters / EARTH_RADIUS
        val bearingRad = Math.toRadians(bearingDegrees)
        val lat1 = Math.toRadians(start.lat)
        val lon1 = Math.toRadians(start.lng)

        val lat2 = kotlin.math.asin(
            sin(lat1) * cos(distRatio) +
            cos(lat1) * sin(distRatio) * cos(bearingRad)
        )
        val lon2 = lon1 + atan2(
            sin(bearingRad) * sin(distRatio) * cos(lat1),
            cos(distRatio) - sin(lat1) * sin(lat2)
        )
        return MapPoint(Math.toDegrees(lat2), Math.toDegrees(lon2))
    }
}
