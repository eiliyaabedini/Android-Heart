package de.lizsoft.heart.interfaces.map.service

import de.lizsoft.heart.interfaces.ResponseResult
import de.lizsoft.heart.interfaces.map.Coordinate
import de.lizsoft.heart.interfaces.map.model.PlaceModel
import de.lizsoft.heart.interfaces.map.model.PredictionModel
import io.reactivex.Maybe

interface PlacesService {

    fun getPredictionsResult(
        locationName: String,
        coordinate: Coordinate? = null
    ): Maybe<ResponseResult<List<PredictionModel>>>

    fun fetchPlace(placeId: String): Maybe<ResponseResult<PlaceModel>>
}