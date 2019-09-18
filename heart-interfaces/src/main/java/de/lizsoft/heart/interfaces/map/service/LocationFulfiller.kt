package de.lizsoft.heart.interfaces.map.service

import android.location.Location

interface LocationFulfiller {

    fun fulfill(location: Location): de.lizsoft.heart.interfaces.map.Location
    fun fulfill(location: de.lizsoft.heart.interfaces.map.Location): de.lizsoft.heart.interfaces.map.Location
}