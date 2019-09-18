package de.lizsoft.travelcheck.model.common

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel

data class HotelOfferValidationTravelModel(
    val offerId: String,
    val statusCode: String,
    val statusCodeText: String,
    val statusType: String,
    val statusSuccess: Boolean,
    val startDate: String,
    val endDate: String,
    val totalPrice: PriceTravelModel
) : ViewModel