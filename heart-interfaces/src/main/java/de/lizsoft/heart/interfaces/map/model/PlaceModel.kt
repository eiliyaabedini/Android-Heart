package de.lizsoft.heart.interfaces.map.model

import de.lizsoft.heart.interfaces.map.Coordinate

data class PlaceModel(
    val placeId: String,
    val coordinate: Coordinate
)