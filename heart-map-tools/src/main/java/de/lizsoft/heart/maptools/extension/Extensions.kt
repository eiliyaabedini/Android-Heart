package de.lizsoft.heart.maptools.extension

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.RectangularBounds
import de.lizsoft.heart.interfaces.map.Coordinate

fun Location.toLatLngBounds(): LatLngBounds {
    return LatLngBounds(
          LatLng(latitude, longitude),
          LatLng(latitude, longitude)
    )
}

fun Coordinate.toLatLngBounds(): LatLngBounds {
    return LatLngBounds(
          LatLng(latitude, longitude),
          LatLng(latitude, longitude)
    )
}

fun Coordinate.toRectangularBounds(): RectangularBounds {
    return RectangularBounds.newInstance(
          LatLng(latitude, longitude),
          LatLng(latitude, longitude)
    )
}
