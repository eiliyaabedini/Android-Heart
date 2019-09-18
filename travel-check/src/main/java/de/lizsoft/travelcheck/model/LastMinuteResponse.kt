package de.lizsoft.travelcheck.model

import de.lizsoft.travelcheck.model.common.CityTravelModel
import de.lizsoft.travelcheck.model.common.PriceTravelModel
import de.lizsoft.travelcheck.model.common.RegionTravelModel
import de.lizsoft.travelcheck.model.common.WeatherTravelModel
import de.lizsoft.travelcheck.repository.RepositoryType

data class LastMinuteResponse(
      val data: LastMinuteData
) {

    data class LastMinuteData(
          val items: List<LastMinuteDataItem>
    ) {

        data class LastMinuteDataItem(
              val id: String,
              val name: String,
              val price: LastMinuteDataItemPrice,
              val regions: List<LastMinuteDataItemRegions>
        ) {

            data class LastMinuteDataItemPrice(
                  val original: LastMinuteDataItemPriceOriginal
            ) {

                data class LastMinuteDataItemPriceOriginal(
                      val min: LastMinuteDataItemPriceOriginalMin
                ) {

                    data class LastMinuteDataItemPriceOriginalMin(
                          val amount: Int,
                          val currency: String
                    )
                }
            }

            data class LastMinuteDataItemRegions(
                  val region: LastMinuteDataItemRegionsRegion,
                  val offer: LastMinuteDataItemOffer
            ) {

                data class LastMinuteDataItemRegionsRegion(
                      val id: Int,
                      val topRegion: Int,
                      val groupId: Int,
                      val hotelCount: Int,
                      val name: String,
                      val country: String,
                      val airport: String,
                      val groupName: String,
                      val weather: LastMinuteDataItemRegionsRegionWeather
                ) {

                    data class LastMinuteDataItemRegionsRegionWeather(
                          val air: String,
                          val water: String
                    )
                }

                data class LastMinuteDataItemOffer(
                      val price: LastMinuteDataItemOfferPrice,
                      val hotel: LastMinuteDataItemHotel
                ) {

                    data class LastMinuteDataItemOfferPrice(
                          val original: LastMinuteDataItemOfferPriceOriginal
                    ) {

                        data class LastMinuteDataItemOfferPriceOriginal(
                              val amount: Double,
                              val currency: String
                        )
                    }

                    data class LastMinuteDataItemHotel(
                          val id: Int
                    )
                }
            }
        }
    }

    fun convert(favouriteCities: List<Int>): List<RegionTravelModel> {
        return data.items
              .map { item ->
                  RegionTravelModel(
                        id = item.id,
                        name = item.name,
                        repositoryType = RepositoryType.LASTMINUTE,
                        cities = item.regions
                              .map { region ->
                                  CityTravelModel(
                                        repositoryType = RepositoryType.LASTMINUTE,
                                        id = region.region.id,
                                        regionId = region.region.groupId,
                                        hotelId = region.offer.hotel.id,
                                        regionName = region.region.groupName,
                                        name = region.region.name,
                                        country = region.region.country,
                                        airportCode = region.region.airport,
                                        hotelCount = region.region.hotelCount,
                                        weather = WeatherTravelModel(
                                              air = region.region.weather.air,
                                              water = region.region.weather.water
                                        ),
                                        price = PriceTravelModel(
                                              amount = region.offer.price.original.amount,
                                              currency = region.offer.price.original.currency
                                        ),
                                        isFavourite = favouriteCities.contains(region.region.id)
                                  )
                              }
                  )
              }
    }
}