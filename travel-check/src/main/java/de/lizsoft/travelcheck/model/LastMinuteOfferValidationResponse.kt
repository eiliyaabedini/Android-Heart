package de.lizsoft.travelcheck.model

import de.lizsoft.travelcheck.model.common.HotelOfferValidationTravelModel
import de.lizsoft.travelcheck.model.common.PriceTravelModel

data class LastMinuteOfferValidationResponse(
    val data: LastMinuteOfferValidationData
) {

    data class LastMinuteOfferValidationData(
        val status: LastMinuteOfferValidationDataStatus,
        val offer: LastMinuteOfferValidationDataOffer,
        val price: LastMinuteOfferValidationDataPrice
    ) {

        data class LastMinuteOfferValidationDataStatus(
            val code: String,
            val codeText: String,
            val type: String,
            val success: Boolean
        )

        data class LastMinuteOfferValidationDataOffer(
            val id: String,
            val tripDates: LastMinuteOfferValidationDataOfferTripDates
        ) {

            data class LastMinuteOfferValidationDataOfferTripDates(
                val start: LastMinuteOfferValidationDataOfferTripDatesDate,
                val end: LastMinuteOfferValidationDataOfferTripDatesDate
            ) {

                data class LastMinuteOfferValidationDataOfferTripDatesDate(
                    val date: String
                )
            }
        }

        data class LastMinuteOfferValidationDataPrice(
            val total: LastMinuteOfferValidationDataPriceTotal
        ) {

            data class LastMinuteOfferValidationDataPriceTotal(
                val amount: Double,
                val currency: String
            )
        }
    }

    fun convert(): HotelOfferValidationTravelModel {
        return HotelOfferValidationTravelModel(
              offerId = data.offer.id,
              statusCode = data.status.code,
              statusCodeText = data.status.codeText,
              statusType = data.status.type,
              statusSuccess = data.status.success,
              totalPrice = PriceTravelModel(
                    amount = data.price.total.amount,
                    currency = data.price.total.currency
              ),
              startDate = data.offer.tripDates.start.date,
              endDate = data.offer.tripDates.end.date
        )
    }
}