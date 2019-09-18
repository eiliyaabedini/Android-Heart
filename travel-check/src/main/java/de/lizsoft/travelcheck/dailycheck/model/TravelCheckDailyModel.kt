package de.lizsoft.travelcheck.dailycheck.model

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.travelcheck.model.common.CityTravelModel
import de.lizsoft.travelcheck.model.common.PriceTravelModel
import java.io.Serializable

data class TravelCheckDailyModel(
      val city: CityTravelModel,
      private val timePrices: MutableList<TravelCheckDailyTimePriceModel> = mutableListOf(),
      val updatedAt: Long = System.currentTimeMillis()
) : ViewModel, Serializable {

    fun addPrice(date: String, price: PriceTravelModel) {
        timePrices.removeAll { it.date == date }

        timePrices.add(
              TravelCheckDailyTimePriceModel(
                    date = date,
                    price = price
              )
        )
    }

    fun getTimePrices(): List<TravelCheckDailyTimePriceModel> = timePrices

    fun getMinPrice() : TravelCheckDailyMinPriceModel {
        val minimumModel = timePrices.minBy { it.price.amount }

        return TravelCheckDailyMinPriceModel(
              date = minimumModel!!.date,
              priceModel = minimumModel.price
        )
    }
}