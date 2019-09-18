package de.lizsoft.travelcheck.model.common

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.travelcheck.repository.RepositoryType

data class HotelOfferTravelModel(
      val repositoryType: RepositoryType,
      val id: String,
      val hotelId: Int,
      val isExclusive: Boolean,
      val price: PriceTravelModel,
      val duration: Int,
      val categoryOffer: Float
) : ViewModel