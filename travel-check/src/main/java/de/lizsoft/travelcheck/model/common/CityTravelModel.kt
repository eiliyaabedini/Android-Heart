package de.lizsoft.travelcheck.model.common

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.travelcheck.repository.RepositoryType

data class CityTravelModel(
      val repositoryType: RepositoryType,
      val id: Int,
      val regionId: Int,
      val hotelId: Int,
      val regionName: String,
      val name: String,
      val country: String,
      val airportCode: String,
      val hotelCount: Int,
      val weather: WeatherTravelModel,
      val price: PriceTravelModel,
      val isFavourite: Boolean
) : ViewModel {

    fun getUniqueKey(): String = "${id}_$regionId"
}