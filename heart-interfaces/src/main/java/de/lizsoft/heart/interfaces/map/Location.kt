package de.lizsoft.heart.interfaces.map

import java.io.Serializable

data class Location(
    val duration: Double = 0.0,
    val name: String?,
    val address: String,
    val coordinate: Coordinate
) : Serializable {

    companion object {
        val EMPTY: Location = Location(
              name = "",
              address = "",
              coordinate = Coordinate()
        )
    }

    fun isEmpty(): Boolean = coordinate.latitude == 0.0 && coordinate.longitude == 0.0
}