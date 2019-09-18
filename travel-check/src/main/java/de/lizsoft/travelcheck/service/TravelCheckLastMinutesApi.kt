package de.lizsoft.travelcheck.service

import de.lizsoft.travelcheck.model.LastMinuteHotelOffersResponse
import de.lizsoft.travelcheck.model.LastMinuteOfferValidationResponse
import de.lizsoft.travelcheck.model.LastMinuteResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TravelCheckLastMinutesApi {

    //    @GET("api/regiontree?v=a89de52be5e2&currency=EUR&ibe=package&lang=de-DE&sc=DE")
    //    fun getAllDestinations(): Single<LastMinuteResponse>

//    @GET("api/region?v=a89de52be5e2&int_detail=de_pauschal_themenurlaub_Thema1-10979&srtHot=5&srtOff=5")
    @GET("api/region?v=a89de52be5e2&int_detail=de_pauschal_themenurlaub_Thema1-10979")
    fun getAllDestinationsInfo(
        @Query("ddate") startDate: String,
        @Query("rdate") endDate: String,
        @Query("stars") minStars: String,
        @Query("depap") departureAirportCode: String,
        @Query("adult") adultsCount: Int,
        @Query("child") childAge: Int,
        @Query("dur") durationDays: Int,
        @Query("board") board: Int = 3, //HalfBoard
        @Query("trans") trans: Int? = null //Transportation
    ): Single<LastMinuteResponse>

//    @GET("api/offer?lang=de-DE&srtHot=5&srtOff=5")
    @GET("api/offer")
    fun getHotelOffers(
        @Query("rid") cityId: Int,
        @Query("aid") hotelId: Int,
        @Query("ddate") startDate: String,
        @Query("rdate") endDate: String,
        @Query("stars") minStars: String,
        @Query("depap") departureAirportCode: String,
        @Query("adult") adultsCount: Int,
        @Query("child") childAge: Int,
        @Query("dur") durationDays: Int,
        @Query("board") board: Int = 3, //HalfBoard,
        @Query("resPerPagOff") pageSize: Int = 20 //Number of offers in each page
    ): Single<LastMinuteHotelOffersResponse>

    //    @GET("api/availability?v=c3ad4ccad143&amount=312&board=3&child=2&currency=EUR&ddate=2019-08-15&depap=HAM&dur=7&in=Palma+de+Mallorca;PMI&lang=de-DE&oid=2OBNWMJZB3DTSKPF126TUPCBK21FDZOCUUYUJK12EA6V36YMAKTYXL2LFXKZF8961M9X6YR912HWS7UO84OZLVGF1M8NLVCMP8YPNWX1&out=Hamburg;HAM&price=312&rdate=2019-09-30&rid=35&srtHot=5&srtOff=5&stars=4")
//    @GET("api/availability?lang=de-DE&srtHot=5&srtOff=5")
    @GET("api/availability")
    fun getHotelOfferValidation(
        @Query("oid") offerId: String,
        @Query("rid") cityId: Int,
        @Query("aid") hotelId: Int,
        @Query("ddate") startDate: String,
        @Query("rdate") endDate: String,
        @Query("stars") minStarts: String,
        @Query("depap") departureAirportCode: String,
        @Query("adult") adultsCount: Int,
        @Query("child") childAge: Int,
        @Query("dur") durationDays: Int,
        @Query("board") board: Int = 3 //HalfBoard
    ): Single<LastMinuteOfferValidationResponse>
}