package de.lizsoft.heart.maptools.services

import de.lizsoft.heart.interfaces.ResponseResult
import de.lizsoft.heart.interfaces.common.rx.flatMapObservableData
import de.lizsoft.heart.interfaces.common.rx.flatMapSingleData
import de.lizsoft.heart.interfaces.common.rx.mapData
import de.lizsoft.heart.interfaces.common.rx.toListData
import de.lizsoft.heart.interfaces.map.Location
import de.lizsoft.heart.interfaces.map.service.AddressService
import de.lizsoft.heart.interfaces.map.service.CurrentLocation
import de.lizsoft.heart.interfaces.map.service.PlacesService
import io.reactivex.Observable
import io.reactivex.Single
import org.koin.core.scope.Scope

class AddressServiceImp(
      private val placesService: PlacesService,
      private val currentLocation: CurrentLocation
) : AddressService {

    override fun getSuggestionsResult(
        locationName: String,
        scope: Scope
    ): Single<ResponseResult<List<Location>>> {
        return currentLocation.getCurrentLocation(scope)
              .defaultIfEmpty(ResponseResult.Success(Location.EMPTY))
              .flatMapSingleData { currentLocation ->
                  placesService.getPredictionsResult(locationName, currentLocation.coordinate)
                        .mapData { autocompletePredictions ->
                            autocompletePredictions.map { prediction ->
                                placesService.fetchPlace(prediction.placeId) to prediction
                            }
                        }
                        .flatMapObservableData { pairsList ->
                            Observable.fromIterable(pairsList)
                                  .flatMapMaybe { (maybe, prediction) ->
                                      maybe.mapData { place -> place to prediction }
                                  }
                        }
                        .mapData { (place, prediction) ->
                            Location(
                                  name = prediction.primaryText,
                                  address = prediction.fullText,
                                  coordinate = place.coordinate
                            )
                        }
                        .toListData()
                        .onErrorReturn { ResponseResult.Failure(it) }
              }
              .onErrorReturn { ResponseResult.Failure(it) }
    }

}