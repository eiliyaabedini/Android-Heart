package de.lizsoft.heart.maptools

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import de.lizsoft.heart.interfaces.common.DrawableUtils
import de.lizsoft.heart.interfaces.common.TextUtils
import de.lizsoft.heart.interfaces.map.Location
import de.lizsoft.heart.maptools.mapbuilder.MarkerType
import org.koin.core.KoinComponent
import org.koin.core.inject

object MapMarkerFactory : KoinComponent {

    private const val PRIORITY_HIGH: Float = 1.0f
    private const val PRIORITY_MEDIUM_HIGH: Float = 0.8f
    private const val PRIORITY_MEDIUM: Float = 0.5f
    const val PRIORITY_LOW: Float = 0.1f

    private val drawableUtils: DrawableUtils by inject()
    private val textUtils: TextUtils by inject()

    private val circleBitmapDescriptor: BitmapDescriptor by lazy {
        BitmapDescriptorFactory.fromBitmap(drawableUtils.getBitmap(R.drawable.ic_circle_pin_white))
    }

    private val circlePrimaryBitmapDescriptor: BitmapDescriptor by lazy {
        BitmapDescriptorFactory.fromBitmap(drawableUtils.getBitmap(R.drawable.ic_circle_pin_primary))
    }

    private val pinPrimaryBitmapDescriptor: BitmapDescriptor by lazy {
        BitmapDescriptorFactory.fromBitmap(drawableUtils.getBitmap(R.drawable.ic_location_pin_primary_24dp))
    }

    private val pinBlackBitmapDescriptor: BitmapDescriptor by lazy {
        BitmapDescriptorFactory.fromBitmap(drawableUtils.getBitmap(R.drawable.ic_location_pin_black_24dp))
    }

    private val pinCarBlackBitmapDescriptor: BitmapDescriptor by lazy {
        BitmapDescriptorFactory.fromBitmap(drawableUtils.getBitmap(R.drawable.ic_directions_car_black_24dp))
    }

    fun makeMarkerOptionsWithType(markerType: MarkerType, location: Location): MarkerOptions {
        val markerOptions = MarkerOptions()
        markerOptions.position(location.coordinate.toLatLng())

        return when (markerType) {
            is MarkerType.WAYPOINT_SCREEN_WAYPOINT -> {
                markerOptions.icon(circleBitmapDescriptor)
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.zIndex(PRIORITY_MEDIUM_HIGH)
            }

            is MarkerType.WAYPOINT_SCREEN_RECOMMENDATION_WAYPOINT -> {
                markerOptions.icon(
                      BitmapDescriptorFactory.fromBitmap(markerType.avatarBitmap)
                )
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.zIndex(PRIORITY_HIGH)
            }

            is MarkerType.WAYPOINT_SCREEN_ORIGIN -> {
                markerOptions.icon(pinCarBlackBitmapDescriptor)
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.title(textUtils.getString(R.string.ride_details_info_view_start_title))
                markerOptions.snippet(location.address)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.WAYPOINT_SCREEN_DESTINATION -> {
                markerOptions.icon(pinBlackBitmapDescriptor)
                markerOptions.title(textUtils.getString(R.string.ride_details_info_view_end_title))
                markerOptions.snippet(location.address)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.WAYPOINT_SCREEN_RECOMMENDATION_ON_THE_FLY -> {
                markerOptions.icon(
                      BitmapDescriptorFactory.fromBitmap(markerType.avatarBitmap)
                )
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.DRIVER_REVIEW_SCREEN_WAYPOINT -> {
                markerOptions.icon(circleBitmapDescriptor)
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.zIndex(PRIORITY_HIGH)
            }

            is MarkerType.DRIVER_REVIEW_SCREEN_ORIGIN -> {
                markerOptions.icon(pinCarBlackBitmapDescriptor)
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.title(textUtils.getString(R.string.ride_details_info_view_start_title))
                markerOptions.snippet(location.address)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.DRIVER_REVIEW_SCREEN_DESTINATION -> {
                markerOptions.icon(pinBlackBitmapDescriptor)
                markerOptions.title(textUtils.getString(R.string.ride_details_info_view_end_title))
                markerOptions.snippet(location.address)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.RIDE_FEED_DRIVER_ORIGIN -> {
                markerOptions.icon(pinCarBlackBitmapDescriptor)
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.RIDE_FEED_DRIVER_DESTINATION -> {
                markerOptions.icon(pinBlackBitmapDescriptor)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.RIDE_FEED_PASSENGER_ORIGIN -> {
                markerOptions.icon(
                      BitmapDescriptorFactory.fromBitmap(markerType.avatarBitmap)
                )
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.RIDE_FEED_PASSENGER_DESTINATION -> {
                markerOptions.icon(pinBlackBitmapDescriptor)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.RIDE_DETAILS_PASSENGER_ORIGIN -> {
                markerOptions.icon(
                      BitmapDescriptorFactory.fromBitmap(markerType.avatarBitmap)
                )
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.title(textUtils.getString(R.string.ride_details_info_view_start_title))
                markerOptions.snippet(location.address)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.RIDE_DETAILS_DRIVER_ORIGIN -> {
                markerOptions.icon(pinCarBlackBitmapDescriptor)
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.title(textUtils.getString(R.string.ride_details_info_view_start_title))
                markerOptions.snippet(location.address)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.RIDE_DETAILS_DESTINATION -> {
                markerOptions.icon(pinBlackBitmapDescriptor)
                markerOptions.title(textUtils.getString(R.string.ride_details_info_view_end_title))
                markerOptions.snippet(location.address)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.RIDE_DETAILS_PASSENGER_PICKUP -> {
                markerOptions.icon(
                      BitmapDescriptorFactory.fromBitmap(markerType.avatarBitmap)
                )
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.title(textUtils.getString(R.string.ride_details_info_view_pickup_title))
                markerOptions.snippet(location.address)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.RIDE_DETAILS_DRIVER_PICKUP -> {
                markerOptions.icon(pinCarBlackBitmapDescriptor)
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.title(textUtils.getString(R.string.ride_details_info_view_pickup_title))
                markerOptions.snippet(location.address)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }

            is MarkerType.RIDE_DETAILS_DROPOFF_POINT -> {
                markerOptions.icon(circlePrimaryBitmapDescriptor)
                markerOptions.anchor(0.5F, 0.5F)
                markerOptions.title(textUtils.getString(R.string.ride_details_info_view_drop_off_title))
                markerOptions.snippet(location.address)
                markerOptions.zIndex(PRIORITY_MEDIUM)
            }
        }
    }
}