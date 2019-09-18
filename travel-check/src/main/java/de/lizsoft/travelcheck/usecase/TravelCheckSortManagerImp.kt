package de.lizsoft.travelcheck.usecase

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.model.common.CityTravelModel
import de.lizsoft.travelcheck.model.common.SortType

class TravelCheckSortManagerImp(
    private val settingsProvider: SettingsProvider
) : TravelCheckSortManager {

    override fun <T : ViewModel> sort(list: List<T>): List<ViewModel> {
        return when (settingsProvider.getSortType()) {
            SortType.NONE -> list
            SortType.PRICE -> list.filterIsInstance(CityTravelModel::class.java).sortedBy { it.price.amount }
            SortType.WATER_TEMPERATURE -> list.filterIsInstance(CityTravelModel::class.java).sortedByDescending { it.weather.water }
            SortType.WEATHER_TEMPERATURE -> list.filterIsInstance(CityTravelModel::class.java).sortedByDescending { it.weather.air }
        }
    }
}