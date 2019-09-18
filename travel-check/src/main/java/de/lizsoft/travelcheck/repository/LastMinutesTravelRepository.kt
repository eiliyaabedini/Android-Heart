package de.lizsoft.travelcheck.repository

import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.rx.ToSingle
import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.model.LastMinuteResponse
import de.lizsoft.travelcheck.model.common.HotelOfferTravelModel
import de.lizsoft.travelcheck.model.common.HotelOfferValidationTravelModel
import de.lizsoft.travelcheck.model.common.RegionTravelModel
import de.lizsoft.travelcheck.service.TravelCheckLastMinutesApi
import io.reactivex.Single

class LastMinutesTravelRepository(
      private val travelCheckLastMinutesApi: TravelCheckLastMinutesApi,
      private val settingsProvider: SettingsProvider, //TODO Remove it from here. Repository has to receive values from outside
      private val reactiveTransformer: ReactiveTransformer
) : TravelRepository {

    private var currentResponse: LastMinuteResponse? = null
    private var currentFLexibleResponse: LastMinuteResponse? = null

    override fun fetchDestinations(forceUpdate: Boolean): Single<List<RegionTravelModel>> {

        val previousResponse = currentResponse

        return if (!forceUpdate && previousResponse != null) {
            previousResponse.ToSingle()
        } else {
            travelCheckLastMinutesApi.getAllDestinationsInfo(
                  startDate = settingsProvider.getStartDate(),
                  endDate = settingsProvider.getEndDate()!!,
                  minStars = settingsProvider.getStars(),
                  departureAirportCode = settingsProvider.getDepartureAirport().code,
                  adultsCount = settingsProvider.getNumberOfAdults(),
                  childAge = settingsProvider.getChildAge(),
                  durationDays = settingsProvider.getDuration()
            )
                  .subscribeOn(reactiveTransformer.ioScheduler())
                  .doOnSuccess { currentResponse = it }
        }
              .map { response ->
                  response.convert(settingsProvider.getFavouriteCities())
              }
    }

    override fun fetchFlexibleDestinations(): Single<List<RegionTravelModel>> {
        return (currentFLexibleResponse?.ToSingle()
              ?: travelCheckLastMinutesApi.getAllDestinationsInfo(
                    startDate = settingsProvider.getStartDate(),
                    endDate = settingsProvider.getEndDate()!!,
                    minStars = settingsProvider.getStars(),
                    departureAirportCode = "TXL,BRE,SXF,DUS,FRA,HAJ,LBC",
                    adultsCount = settingsProvider.getNumberOfAdults(),
                    childAge = settingsProvider.getChildAge(),
                    durationDays = settingsProvider.getDuration(),
                    trans = 1
              )
                    .subscribeOn(reactiveTransformer.ioScheduler())
                    .doOnSuccess { currentFLexibleResponse = it })
              .map { response ->
                  response.convert(settingsProvider.getFavouriteCities())
              }
    }

    override fun getHotelOffers(cityId: Int, hotelId: Int, pageSize: Int): Single<List<HotelOfferTravelModel>> {
        return travelCheckLastMinutesApi.getHotelOffers(
              cityId = cityId,
              hotelId = hotelId,
              startDate = settingsProvider.getStartDate(),
              endDate = settingsProvider.getEndDate()!!,
              minStars = settingsProvider.getStars(),
              departureAirportCode = settingsProvider.getDepartureAirport().code,
              adultsCount = settingsProvider.getNumberOfAdults(),
              childAge = settingsProvider.getChildAge(),
              durationDays = settingsProvider.getDuration()
        )
              .subscribeOn(reactiveTransformer.ioScheduler())
              .map { it.convert() }
    }

    override fun getHotelOfferValidation(
          offerId: String,
          cityId: Int,
          hotelId: Int
    ): Single<HotelOfferValidationTravelModel> {
        return travelCheckLastMinutesApi.getHotelOfferValidation(
              offerId = offerId,
              cityId = cityId,
              hotelId = hotelId,
              startDate = settingsProvider.getStartDate(),
              endDate = settingsProvider.getEndDate()!!,
              minStarts = settingsProvider.getStars(),
              departureAirportCode = settingsProvider.getDepartureAirport().code,
              adultsCount = settingsProvider.getNumberOfAdults(),
              childAge = settingsProvider.getChildAge(),
              durationDays = settingsProvider.getDuration()
        )
              .map { it.convert() }
    }


    override fun getCityOfferUrl(cityId: String, withFlexibility: Boolean): String {
        val stringBuilder: StringBuilder = StringBuilder("https://reisen.lastminute.de/hotel?")
        stringBuilder.append("rdate=${settingsProvider.getEndDate()}")
        stringBuilder.append("&ddate=${settingsProvider.getStartDate()}")
        stringBuilder.append("&adult=${settingsProvider.getNumberOfAdults()}")
        stringBuilder.append("&dur=${settingsProvider.getDuration()}")
        stringBuilder.append("&board=3")
        //                                      "&srtOff=5&srtHot=5" +
        stringBuilder.append("&lang=de-DE")
        stringBuilder.append("&stars=${settingsProvider.getStars()}")
        stringBuilder.append("&rid=${cityId}")

        if (settingsProvider.getChildAge() != -1) {
            stringBuilder.append("&child=${settingsProvider.getChildAge()}")
        }

        if (withFlexibility) {
            stringBuilder.append("&depap=TXL,BRE,SXF,DUS,FRA,HAJ,LBC")
            stringBuilder.append("&trans=1")

        } else {
            stringBuilder.append("&depap=${settingsProvider.getDepartureAirport().code}")
        }

        return stringBuilder.toString()
    }
}