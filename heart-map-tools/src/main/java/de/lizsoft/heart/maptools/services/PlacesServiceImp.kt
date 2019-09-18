package de.lizsoft.heart.maptools.services

import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import de.lizsoft.heart.interfaces.ResponseResult
import de.lizsoft.heart.interfaces.common.rx.mapData
import de.lizsoft.heart.interfaces.common.rx.toMaybeResult
import de.lizsoft.heart.interfaces.map.Coordinate
import de.lizsoft.heart.interfaces.map.model.PlaceModel
import de.lizsoft.heart.interfaces.map.model.PredictionModel
import de.lizsoft.heart.interfaces.map.service.PlacesService
import de.lizsoft.heart.maptools.extension.toRectangularBounds
import io.reactivex.Maybe

class PlacesServiceImp(
    private val placesClient: PlacesClient,
    private val token: AutocompleteSessionToken
) : PlacesService {

    override fun getPredictionsResult(
        locationName: String,
        coordinate: Coordinate?
    ): Maybe<ResponseResult<List<PredictionModel>>> {

        val request = FindAutocompletePredictionsRequest.builder()
              .setLocationBias(coordinate?.toRectangularBounds())
              .setQuery(locationName)
              .setTypeFilter(TypeFilter.GEOCODE)
              .setSessionToken(token)
              .build()

        return placesClient.findAutocompletePredictions(request)
              .toMaybeResult()
              .mapData { findAutocompletePredictionsResponse -> findAutocompletePredictionsResponse.autocompletePredictions }
              .mapData { autocompletePredictions ->
                  autocompletePredictions.map { autocompletePrediction ->
                      PredictionModel(
                            placeId = autocompletePrediction.placeId,
                            primaryText = autocompletePrediction.getPrimaryText(null).toString(),
                            secondaryText = autocompletePrediction.getSecondaryText(null).toString(),
                            fullText = autocompletePrediction.getFullText(null).toString()
                      )
                  }
              }
    }

    override fun fetchPlace(placeId: String): Maybe<ResponseResult<PlaceModel>> {
        return placesClient.fetchPlace(
              FetchPlaceRequest
                    .builder(
                          placeId,
                          listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                    )
                    .build()
        )
              .toMaybeResult()
              .mapData { it.place }
              .mapData { place ->
                  PlaceModel(
                        placeId = placeId,
                        coordinate = Coordinate(
                              latitude = place.latLng?.latitude ?: 0.0,
                              longitude = place.latLng?.longitude ?: 0.0
                        )
                  )
              }
    }
}