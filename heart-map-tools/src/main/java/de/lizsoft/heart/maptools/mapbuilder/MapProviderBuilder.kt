package de.lizsoft.heart.maptools.mapbuilder

import android.graphics.Color
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import de.lizsoft.heart.common.extension.dp2px
import de.lizsoft.heart.interfaces.map.Coordinate
import de.lizsoft.heart.maptools.toLatLng
import io.reactivex.disposables.CompositeDisposable

class MapProviderBuilder {

    private val builderData: BuilderData = BuilderData()

    fun build(
        fragmentManager: FragmentManager
    ): MapProvider =
          MapProvider(fragmentManager, builderData)

    fun disposeBy(disposable: CompositeDisposable): MapProviderBuilder {
        builderData.disposable = disposable

        return this
    }

    fun addMarker(key: String, coordinate: Coordinate): MapProviderBuilder {
        builderData.markers[key] = MarkerOptions().position(coordinate.toLatLng())

        return this
    }

    fun addMainRoute(key: String, route: String): MapProviderBuilder {
        return addRoute(
              key,
              PolylineOptions()
                    .addAll(PolyUtil.decode(route))
                    .color(Color.BLUE)
                    .width(dp2px(5))
        )
    }

    fun animateCamera(): MapProviderBuilder {
        builderData.animateToDefaultLocation = true

        return this
    }

    fun makeEverythingVisible(): MapProviderBuilder {
        builderData.autoZoomToMakeEverythingVisible = true

        return this
    }

    fun liteMode(): MapProviderBuilder {
        builderData.liteMode = true

        return this
    }

    fun addPadding(): MapProviderBuilder {
        builderData.mapPadding = dp2px(32).toInt()

        return this
    }

    fun setDefaultLocationCurrentLocation(zoomLevel: Float): MapProviderBuilder {
        builderData.defaultLocationCurrentLocation = true
        builderData.defaultLocationZoom = zoomLevel

        return this
    }

    fun setDefaultLocation(coordinate: Coordinate, zoomLevel: Float): MapProviderBuilder {
        builderData.defaultLocation = coordinate.toLatLng()
        builderData.defaultLocationZoom = zoomLevel

        return this
    }

    private fun addRoute(key: String, polylineOptions: PolylineOptions): MapProviderBuilder {
        builderData.routes[key] = polylineOptions

        return this
    }
}