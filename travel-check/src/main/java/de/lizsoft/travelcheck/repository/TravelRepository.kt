package de.lizsoft.travelcheck.repository

import de.lizsoft.travelcheck.model.common.HotelOfferTravelModel
import de.lizsoft.travelcheck.model.common.HotelOfferValidationTravelModel
import de.lizsoft.travelcheck.model.common.RegionTravelModel
import io.reactivex.Single

interface TravelRepository {

    fun fetchDestinations(forceUpdate: Boolean): Single<List<RegionTravelModel>>
    fun fetchFlexibleDestinations(): Single<List<RegionTravelModel>>

    fun getHotelOffers(
          cityId: Int,
          hotelId: Int,
          pageSize: Int = 20 //Number of offers in each page
    ): Single<List<HotelOfferTravelModel>>

    fun getHotelOfferValidation(
          offerId: String,
          cityId: Int,
          hotelId: Int
    ): Single<HotelOfferValidationTravelModel>


    fun getCityOfferUrl(cityId: String, withFlexibility: Boolean): String
}