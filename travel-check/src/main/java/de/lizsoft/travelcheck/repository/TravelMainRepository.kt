package de.lizsoft.travelcheck.repository

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.travelcheck.model.common.HotelOfferTravelModel
import de.lizsoft.travelcheck.model.common.HotelOfferValidationTravelModel
import io.reactivex.Single

interface TravelMainRepository {

    fun getAllDestinations(forceUpdate: Boolean, showOnlyFavourites: Boolean, combineWithFlexible: Boolean = false): Single<List<ViewModel>>

    fun getHotelOffers(
          repositoryType: RepositoryType,
          cityId: Int,
          hotelId: Int,
          pageSize: Int = 20 //Number of offers in each page
    ): Single<List<HotelOfferTravelModel>>

    fun getHotelOfferValidation(
          repositoryType: RepositoryType,
          offerId: String,
          cityId: Int,
          hotelId: Int
    ): Single<HotelOfferValidationTravelModel>


    fun getCityOfferUrl(
          repositoryType: RepositoryType,
          cityId: String
    ): String
}