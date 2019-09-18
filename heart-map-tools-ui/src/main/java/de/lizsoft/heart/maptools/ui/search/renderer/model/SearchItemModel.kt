package de.lizsoft.heart.maptools.ui.search.renderer.model

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.heart.interfaces.map.Location

sealed class SearchItemModel : ViewModel {

    data class SearchItemModelPrediction(val location: Location) : SearchItemModel() {
        override fun equals(other: Any?): Boolean {
            return other is SearchItemModelPrediction &&
                  other.location == this.location
        }

        override fun hashCode(): Int {
            return location.name.hashCode()
        }
    }

    object SearchItemModelCurrentLocation : SearchItemModel()

    object SearchItemModelSelectOnMap : SearchItemModel()
}