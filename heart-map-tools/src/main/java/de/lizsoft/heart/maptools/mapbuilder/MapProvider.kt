package de.lizsoft.heart.maptools.mapbuilder

import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import de.lizsoft.heart.common.extension.dp2px
import de.lizsoft.heart.interfaces.common.ColorUtils
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.map.Coordinate
import de.lizsoft.heart.interfaces.map.Location
import de.lizsoft.heart.interfaces.map.service.CurrentLocation
import de.lizsoft.heart.interfaces.map.service.LocationFulfiller
import de.lizsoft.heart.maptools.MapMarkerFactory
import de.lizsoft.heart.maptools.R
import de.lizsoft.heart.maptools.toLatLng
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MapProvider internal constructor(
    private val fragmentManager: FragmentManager,
    private val builderData: BuilderData
) : KoinComponent {

    private val markers: MutableMap<String, Marker> = mutableMapOf()
    private val routes: MutableMap<String, Polyline> = mutableMapOf()

    private var mapReadySubject: BehaviorSubject<GoogleMap> = BehaviorSubject.create()
    private var updateCameraSubject: BehaviorSubject<Unit> = BehaviorSubject.create()

    private val colorUtils: ColorUtils by inject()
    private val reactiveTransformer: ReactiveTransformer by inject()
    private val currentLocation: CurrentLocation by inject()
    private val locationFullfiller: LocationFulfiller by inject()

    fun startWith(fragmentResId: Int) {

        val options = GoogleMapOptions()
        options.liteMode(builderData.liteMode)

        val supportMapFragment = SupportMapFragment.newInstance(options)
        fragmentManager.beginTransaction().replace(fragmentResId, supportMapFragment).commit()

        supportMapFragment.getMapAsync { googleMap ->
            onMapReady(googleMap)
        }

        builderData.disposable += updateCameraSubject
              .subscribeOn(reactiveTransformer.ioScheduler())
              .skip(1) //Skip first one as they are already drawn in the map while initialising
              .debounce(500, TimeUnit.MILLISECONDS)
              .switchMapMaybe {
                  mapReadySubject.firstElement()
                        .observeOn(reactiveTransformer.mainThreadScheduler())
                        .doOnSuccess { googleMap ->
                            val bounds = getBoundsForMakeEverythingVisible()
                            googleMap.animateCamera(
                                  CameraUpdateFactory.newLatLngBounds(bounds.build(), builderData.mapPadding)
                            )
                        }
              }
              .subscribe()
    }

    private fun onMapReady(map: GoogleMap) {

        //Add Markers
        builderData.markers.forEach { entry ->
            addMarker(entry.key, entry.value, map)
        }

        //Add Routes
        builderData.routes.forEach { entry ->
            addRoute(entry.key, entry.value, map)
        }

        showDefaultLocation(map)

        mapReadySubject.onNext(map)
    }

    private fun showDefaultLocation(googleMap: GoogleMap) {
        val bounds = when {
            builderData.autoZoomToMakeEverythingVisible -> {
                Timber.d("showDefaultLocation autoZoomToMakeEverythingVisible")
                getBoundsForMakeEverythingVisible()
            }

            else -> LatLngBounds.Builder()
        }

        val defaultLocation = getDefaultLocation()

        defaultLocation?.let {
            bounds.include(it)
        }

        //Dont make animation if there are not any markers or route in the map
        if (builderData.animateToDefaultLocation) {

            //Start from default location if it is provided
            defaultLocation?.let {
                googleMap.moveCamera(
                      CameraUpdateFactory.newLatLngZoom(
                            defaultLocation,
                            builderData.defaultLocationZoom
                      )
                )
            }

            if (hasAnyMarkersOrRoute()) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), builderData.mapPadding))
            }
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), builderData.mapPadding))
        }
    }

    private fun getDefaultLocation(): LatLng? {
        return when {
            builderData.defaultLocation != null -> builderData.defaultLocation

            builderData.defaultLocationCurrentLocation -> {
                currentLocation.getLastReceivedLocation()?.toLatLng()
            }

            else -> null
        }

    }

    private fun getBoundsForMakeEverythingVisible(): LatLngBounds.Builder {
        val builder = LatLngBounds.Builder()

        if (hasAnyMarkersOrRoute()) {
            Timber.d("Requested to zoom to everything is preparing a camera update")

            for (marker in markers.values) {
                builder.include(marker.position)
            }
            for (route in routes.values) {
                route.points.forEach { builder.include(it) }
            }
        }

        return builder
    }

    private fun hasAnyMarkersOrRoute() = markers.isNotEmpty() || routes.isNotEmpty()

    private fun applyInMap(callback: (GoogleMap) -> Unit) {
        builderData.disposable += mapReadySubject.subscribe { googleMap ->
            callback(googleMap)

            if (builderData.autoZoomToMakeEverythingVisible) {
                updateCameraSubject.onNext(Unit)
            }
        }
    }

    fun addMarker(key: String, markerOptions: MarkerOptions, googleMap: GoogleMap) {
        removeMarker(key)
        markers[key] = googleMap.addMarker(markerOptions)
              .apply { tag = key }
    }

    fun removeMarker(key: String): MapProvider {
        markers[key]?.remove()
        markers.remove(key)

        return this
    }

    fun removeAllByGroupKey(groupKey: String): MapProvider {

        markers.keys.filter { it.startsWith(groupKey) }.forEach {
            removeMarker(it)
        }

        routes.keys.filter { it.startsWith(groupKey) }.forEach {
            removeRoute(it)
        }

        return this
    }

    fun removeAllMarkers(): MapProvider {
        markers.forEach { entry ->
            markers[entry.key]?.remove()
        }

        markers.clear()

        return this
    }

    fun addRoute(groupKey: String, key: String, polylineOptions: PolylineOptions): MapProvider {
        return addRoute("${groupKey}_$key", polylineOptions)
    }

    fun addRoute(key: String, polylineOptions: PolylineOptions): MapProvider {
        applyInMap { googleMap ->
            addRoute(key, polylineOptions, googleMap)
        }

        return this
    }

    private fun addRoute(key: String, polylineOptions: PolylineOptions, googleMap: GoogleMap) {
        removeRoute(key)

        polylineOptions.zIndex(MapMarkerFactory.PRIORITY_LOW)
        routes[key] = googleMap.addPolyline(polylineOptions)
    }

    fun removeRoute(key: String): MapProvider {
        routes[key]?.remove()
        routes.remove(key)

        return this
    }

    fun removeAllRoutes(): MapProvider {
        routes.forEach { entry ->
            routes[entry.key]?.remove()
        }

        routes.clear()

        return this
    }

    fun addMarker(groupKey: String, key: String, location: Location, markerType: MarkerType) {
        addMarker("${groupKey}_$key", location, markerType)
    }

    fun addMarker(key: String, location: Location, markerType: MarkerType) {
        applyInMap { googleMap ->
            removeMarker(key)
            markers[key] = googleMap.addMarker(
                  MapMarkerFactory.makeMarkerOptionsWithType(
                        markerType, location
                  )
            ).apply { tag = key }
        }
    }

    fun addWalkingRoute(key: String, start: Location, end: Location) {
        applyInMap { googleMap ->
            removeRoute(key)
            val pattern = listOf(Dot(), Gap(20f))
            routes[key] = googleMap.addPolyline(
                  PolylineOptions()
                        .add(LatLng(start.coordinate.latitude, start.coordinate.longitude))
                        .add(LatLng(end.coordinate.latitude, end.coordinate.longitude))
                        .color(colorUtils.getColor(R.color.black))
                        .pattern(pattern)
                        .zIndex(MapMarkerFactory.PRIORITY_LOW)
            )
        }
    }

    fun addMainRoute(key: String, route: String) {
        applyInMap { googleMap ->
            removeRoute(key)
            routes[key] = googleMap.addPolyline(
                  PolylineOptions()
                        .addAll(PolyUtil.decode(route))
                        .color(colorUtils.getColor(R.color.primary))
                        .width(dp2px(5))
                        .zIndex(MapMarkerFactory.PRIORITY_LOW)
            )
        }
    }

    fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        applyInMap { googleMap ->
            googleMap.setPadding(left, top, right, bottom)
        }
    }

    fun setPadding(padding: Int) {
        applyInMap { googleMap ->
            googleMap.setPadding(padding, padding, padding, padding)
        }
    }

    fun observeMarkerClick(): Observable<String> {
        val subject = BehaviorSubject.create<String>()
        applyInMap { googleMap ->
            googleMap.setOnMarkerClickListener { marker ->
                subject.onNext(marker.tag as String)

                true
            }
        }

        return subject
    }

    companion object {
        fun builder(): MapProviderBuilder = MapProviderBuilder()
    }

    fun cameraTargetAddress(): Location? {
        val cameraTargetPosition: LatLng = mapReadySubject.value?.cameraPosition?.target ?: return null
        val location = Location(
              name = null,
              address = "",
              coordinate = Coordinate(cameraTargetPosition.latitude, cameraTargetPosition.longitude)
        )
        return locationFullfiller.fulfill(location)
    }

    fun setIdleListener(listener: GoogleMap.OnCameraIdleListener?) {
        builderData.disposable += mapReadySubject.subscribe { map ->
            map.setOnCameraIdleListener(listener)
        }
    }

    fun setStartMovingListener(listener: GoogleMap.OnCameraMoveStartedListener?) {
        builderData.disposable += mapReadySubject.subscribe { map ->
            map.setOnCameraMoveStartedListener(listener)
        }
    }
}