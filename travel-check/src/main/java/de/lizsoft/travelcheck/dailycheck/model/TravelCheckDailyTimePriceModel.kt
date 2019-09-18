package de.lizsoft.travelcheck.dailycheck.model

import de.lizsoft.travelcheck.model.common.PriceTravelModel
import java.io.Serializable

data class TravelCheckDailyTimePriceModel(
      val date: String,
      val price: PriceTravelModel,
      val updatedAt: Long = System.currentTimeMillis()
) : Serializable