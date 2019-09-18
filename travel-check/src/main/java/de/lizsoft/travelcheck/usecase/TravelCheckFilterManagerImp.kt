package de.lizsoft.travelcheck.usecase

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.model.common.CityTravelModel
import de.lizsoft.travelcheck.model.common.RegionTravelModel

class TravelCheckFilterManagerImp(
      private val settingsProvider: SettingsProvider
) : TravelCheckFilterManager {

    override fun filterAllDestinations(response: List<RegionTravelModel>): List<ViewModel> {
        return response
              .map { model -> listOf(model, *model.cities.toTypedArray()) }
              .flatten()
              .filter { model ->
                  model is RegionTravelModel ||
                        (model is CityTravelModel)
              }
    }
}