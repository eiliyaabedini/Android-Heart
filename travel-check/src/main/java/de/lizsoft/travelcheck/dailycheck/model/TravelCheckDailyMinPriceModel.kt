package de.lizsoft.travelcheck.dailycheck.model

import de.lizsoft.travelcheck.model.common.PriceTravelModel

data class TravelCheckDailyMinPriceModel(
      val date: String,
      val priceModel: PriceTravelModel
)