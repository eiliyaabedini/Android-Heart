package de.lizsoft.heart.interfaces.map

import android.location.Location
import java.io.Serializable

data class Address(val coordinate: Coordinate, val name: String? = null) : Serializable

data class Coordinate(val latitude: Double, val longitude: Double) : Serializable {
    constructor() : this(0.0, 0.0)
}

fun Coordinate.isSome(): Boolean {
    return latitude > 0f && longitude > 0f
}

fun Coordinate.equalsWithPrecision(other: Coordinate): Boolean {
    return Math.abs(latitude - other.latitude) < 0.00001 && Math.abs(longitude - other.longitude) < 0.00001
}

fun Coordinate.toQueryString(): String? = "$latitude,$longitude"

fun Location.toCoordinate(): Coordinate = Coordinate(latitude, longitude)