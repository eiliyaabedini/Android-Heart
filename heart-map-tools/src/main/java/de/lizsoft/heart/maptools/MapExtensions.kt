package de.lizsoft.heart.maptools

import com.google.android.gms.maps.model.LatLng
import de.lizsoft.heart.interfaces.map.Coordinate

fun Coordinate.toLatLng(): LatLng = LatLng(latitude, longitude)