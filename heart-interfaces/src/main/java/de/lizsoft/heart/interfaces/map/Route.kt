package de.lizsoft.heart.interfaces.map

import java.io.Serializable

data class Route(
    val distance: Double,
    val duration: Double,
    val polyline: String,
    val stopOrder: List<Int>?,
    val stops: List<Location>
) : Serializable