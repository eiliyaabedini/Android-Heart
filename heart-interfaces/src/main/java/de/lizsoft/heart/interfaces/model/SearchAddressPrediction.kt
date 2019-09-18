package de.lizsoft.heart.interfaces.model

import de.lizsoft.heart.interfaces.map.Location
import java.io.Serializable

data class SearchAddressPrediction(
      val location: Location,
      val isCurrentLocation: Boolean
) : Serializable