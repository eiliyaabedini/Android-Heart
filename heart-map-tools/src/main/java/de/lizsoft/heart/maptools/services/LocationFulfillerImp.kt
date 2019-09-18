package de.lizsoft.heart.maptools.services

import android.content.Context
import android.location.Geocoder
import android.location.Location
import de.lizsoft.heart.interfaces.map.Coordinate
import de.lizsoft.heart.interfaces.map.service.LocationFulfiller
import timber.log.Timber

class LocationFulfillerImp(
    private val context: Context
) : LocationFulfiller {

    override fun fulfill(location: Location): de.lizsoft.heart.interfaces.map.Location {
        return fulfillTheLocation(context, location.latitude, location.longitude)
    }

    override fun fulfill(location: de.lizsoft.heart.interfaces.map.Location): de.lizsoft.heart.interfaces.map.Location {
        return fulfillTheLocation(context, location.coordinate.latitude, location.coordinate.longitude)
    }

    private fun fulfillTheLocation(
        context: Context,
        latitude: Double,
        longitude: Double
    ): de.lizsoft.heart.interfaces.map.Location {
        val geocoder = Geocoder(context)

        return try {
            val listAddresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (null != listAddresses && listAddresses.size > 0) {
                Timber.d("getCurrentLocation address:${listAddresses[0]}")

                return de.lizsoft.heart.interfaces.map.Location(
                      name = listAddresses[0].thoroughfare,
                      address = listAddresses[0].getAddressLine(0),
                      coordinate = Coordinate(latitude, longitude)
                )
            }

            de.lizsoft.heart.interfaces.map.Location(name = "", address = "", coordinate = Coordinate(latitude, longitude))
        } catch (ex: Exception) {
            de.lizsoft.heart.interfaces.map.Location(name = "", address = "", coordinate = Coordinate(latitude, longitude))
        }
    }
}