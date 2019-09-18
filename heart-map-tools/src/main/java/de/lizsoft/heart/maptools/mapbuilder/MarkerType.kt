package de.lizsoft.heart.maptools.mapbuilder

import android.graphics.Bitmap

sealed class MarkerType {
    object WAYPOINT_SCREEN_WAYPOINT : MarkerType()
    data class WAYPOINT_SCREEN_RECOMMENDATION_WAYPOINT(val avatarBitmap: Bitmap) : MarkerType()
    object WAYPOINT_SCREEN_ORIGIN : MarkerType()
    object WAYPOINT_SCREEN_DESTINATION : MarkerType()
    data class WAYPOINT_SCREEN_RECOMMENDATION_ON_THE_FLY(val avatarBitmap: Bitmap) : MarkerType()

    object DRIVER_REVIEW_SCREEN_WAYPOINT : MarkerType()
    object DRIVER_REVIEW_SCREEN_ORIGIN : MarkerType()
    object DRIVER_REVIEW_SCREEN_DESTINATION : MarkerType()

    object RIDE_FEED_DRIVER_ORIGIN : MarkerType()
    object RIDE_FEED_DRIVER_DESTINATION : MarkerType()
    data class RIDE_FEED_PASSENGER_ORIGIN(val avatarBitmap: Bitmap) : MarkerType()
    object RIDE_FEED_PASSENGER_DESTINATION : MarkerType()

    //Ride details pins
    object RIDE_DETAILS_DRIVER_ORIGIN : MarkerType()
    object RIDE_DETAILS_DRIVER_PICKUP : MarkerType()
    object RIDE_DETAILS_DROPOFF_POINT : MarkerType()
    data class RIDE_DETAILS_PASSENGER_ORIGIN(val avatarBitmap: Bitmap) : MarkerType()
    data class RIDE_DETAILS_PASSENGER_PICKUP(val avatarBitmap: Bitmap) : MarkerType()
    object RIDE_DETAILS_DESTINATION : MarkerType()
}