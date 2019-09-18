package de.lizsoft.travelcheck.model.common

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.travelcheck.repository.RepositoryType

data class RegionTravelModel(
      val id: String,
      val name: String,
      val repositoryType: RepositoryType,
      val cities: List<CityTravelModel>
) : ViewModel