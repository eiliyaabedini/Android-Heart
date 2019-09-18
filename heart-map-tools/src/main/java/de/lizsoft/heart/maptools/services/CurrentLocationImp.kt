package de.lizsoft.heart.maptools.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import de.lizsoft.heart.interfaces.ResponseResult
import de.lizsoft.heart.interfaces.common.LocalStorageManager
import de.lizsoft.heart.interfaces.common.PermissionHandler
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.rx.doOnData
import de.lizsoft.heart.interfaces.common.rx.mapData
import de.lizsoft.heart.interfaces.common.rx.toMaybeResult
import de.lizsoft.heart.interfaces.map.Coordinate
import de.lizsoft.heart.interfaces.map.service.CurrentLocation
import de.lizsoft.heart.interfaces.map.service.LocationFulfiller
import io.reactivex.Maybe
import io.reactivex.subjects.MaybeSubject
import org.koin.core.scope.Scope

//This class has to be alive in application scope?
class CurrentLocationImp(
      private val context: Context,
      private val permissionHandler: PermissionHandler,
      private val localStorageManager: LocalStorageManager,
      private val locationFulfiller: LocationFulfiller,
      private val reactiveTransformer: ReactiveTransformer
) : CurrentLocation() {

    private var request = LocationRequest.create() //standard GMS LocationRequest
          .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
          .setNumUpdates(1)
          .setInterval(100)

    val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(scope: Scope): Maybe<ResponseResult<de.lizsoft.heart.interfaces.map.Location>> {

        return permissionHandler.getPermission(
              scope,
              Manifest.permission.ACCESS_FINE_LOCATION
        )
              .filter { it }
              .flatMap {
                  fusedLocationClient.lastLocation
                        .toMaybeResult()
                        .switchIfEmpty(
                              requestNewLocation(fusedLocationClient)
                        )

              }
              .subscribeOn(reactiveTransformer.mainThreadScheduler())
              .observeOn(reactiveTransformer.ioScheduler())
              .mapData { locationFulfiller.fulfill(it) }
              .doOnData { saveLastLocation(it.coordinate) }
    }

    override fun getLastReceivedLocation(): Coordinate? {
        return retrieveLastLocation()
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation(fusedLocationClient: FusedLocationProviderClient): Maybe<ResponseResult<Location>> {
        val subject = MaybeSubject.create<ResponseResult<Location>>()
        fusedLocationClient.requestLocationUpdates(request, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                subject.onSuccess(ResponseResult.Success(locationResult.lastLocation))
            }
        }, Looper.getMainLooper())



        return subject
    }

    private fun saveLastLocation(lastLocation: Coordinate) {
        localStorageManager.saveByKey(LAST_RECEIVED_LOCATION_MODEL, lastLocation)
    }

    private fun retrieveLastLocation(): Coordinate? {
        return localStorageManager.getByKey(LAST_RECEIVED_LOCATION_MODEL, Coordinate::class.java)
    }

    companion object {
        const val LAST_RECEIVED_LOCATION_MODEL = "de.lizsoft.heart.maptools.services.LastReceivedLocation"
    }
}