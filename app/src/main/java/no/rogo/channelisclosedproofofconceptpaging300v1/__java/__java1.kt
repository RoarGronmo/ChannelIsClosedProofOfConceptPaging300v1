package no.rogo.channelisclosedproofofconceptpaging300v1.__java

import android.location.Location

/**
 * Created by Roar on 22.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
internal object __java1 {
    fun calculateDistance(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Double {
        val results = FloatArray(3)
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results)
        return results[0].toDouble()
    }
}