package de.lizsoft.heart.interfaces.map

import java.io.Serializable
import java.util.Date

data class LocationWithTime(
    val time: Date?,
    val name: String?,
    val address: String,
    val coordinate: Coordinate
) : Serializable

fun LocationWithTime.toLocation(): Location {
    return Location(
          name = name,
          address = address,
          coordinate = coordinate
    )
}