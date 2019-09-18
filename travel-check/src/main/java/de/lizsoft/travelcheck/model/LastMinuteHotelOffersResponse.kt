package de.lizsoft.travelcheck.model

import de.lizsoft.travelcheck.model.common.HotelOfferTravelModel
import de.lizsoft.travelcheck.model.common.PriceTravelModel
import de.lizsoft.travelcheck.repository.RepositoryType

data class LastMinuteHotelOffersResponse(
      val data: LastMinuteData
) {

    data class LastMinuteData(
          val items: List<LastMinuteHotelOfferDataItem>
    ) {

        data class LastMinuteHotelOfferDataItem(
              val id: String,
              val isExclusive: Boolean,
              val price: LastMinuteHotelOfferDataItemPrice,
              val duration: Int,
              val categoryOffer: Float,
              val hotel: LastMinuteHotelOfferDataItemHotel

        ) {

            data class LastMinuteHotelOfferDataItemPrice(
                  val original: LastMinuteHotelOfferDataItemOfferPriceOriginal
            ) {

                data class LastMinuteHotelOfferDataItemOfferPriceOriginal(
                      val amount: Double,
                      val currency: String
                )
            }

            data class LastMinuteHotelOfferDataItemHotel(
                  val id: Int
            )
        }
    }

    fun convert(): List<HotelOfferTravelModel> {
        return data.items
              .map { item ->
                  HotelOfferTravelModel(
                        repositoryType = RepositoryType.LASTMINUTE,
                        id = item.id,
                        hotelId = item.hotel.id,
                        isExclusive = item.isExclusive,
                        price = PriceTravelModel(
                              amount = item.price.original.amount,
                              currency = item.price.original.currency
                        ),
                        duration = item.duration,
                        categoryOffer = item.categoryOffer
                  )
              }
    }
}