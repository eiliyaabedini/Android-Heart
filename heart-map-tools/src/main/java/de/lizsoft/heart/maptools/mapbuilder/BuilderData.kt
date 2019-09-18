package de.lizsoft.heart.maptools.mapbuilder

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import io.reactivex.disposables.CompositeDisposable

internal data class BuilderData(
    internal var disposable: CompositeDisposable = CompositeDisposable(),
    internal var autoZoomToMakeEverythingVisible: Boolean = false,
    internal var mapPadding: Int = 10,
    internal var defaultLocationCurrentLocation: Boolean = false,
    internal var liteMode: Boolean = false,
    internal var defaultLocation: LatLng? = null,
    internal var defaultLocationZoom: Float = 10.0f,
    internal var animateToDefaultLocation: Boolean = false,
    internal val markers: MutableMap<String, MarkerOptions> = mutableMapOf(),
    internal val routes: MutableMap<String, PolylineOptions> = mutableMapOf()
)