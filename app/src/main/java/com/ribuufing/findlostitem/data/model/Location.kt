package com.ribuufing.findlostitem.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    fun distanceTo(other: Location): Float {
        val result = FloatArray(1)
        android.location.Location.distanceBetween(
            latitude, longitude,
            other.latitude, other.longitude,
            result
        )
        return result[0]
    }
}
