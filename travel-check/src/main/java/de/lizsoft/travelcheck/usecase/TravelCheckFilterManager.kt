package de.lizsoft.travelcheck.usecase

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.travelcheck.model.common.RegionTravelModel

interface TravelCheckFilterManager {

    fun filterAllDestinations(response: List<RegionTravelModel>): List<ViewModel>
}