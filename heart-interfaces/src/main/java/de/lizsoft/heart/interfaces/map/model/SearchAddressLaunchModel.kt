package de.lizsoft.heart.interfaces.map.model

import de.lizsoft.heart.interfaces.map.Location
import java.io.Serializable

class SearchAddressLaunchModel(val showCurrentLocationRow: Boolean = true,
                               val showSelectOnMapRow: Boolean = true,
                               val defaultLocation: Location? = null,
                               val addressTypeName: String? = "") : Serializable