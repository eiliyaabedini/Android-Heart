package de.lizsoft.heart.interfaces.map.service

import de.lizsoft.heart.interfaces.ResponseResult
import de.lizsoft.heart.interfaces.map.Coordinate
import de.lizsoft.heart.interfaces.map.Location
import io.reactivex.Maybe
import org.koin.core.scope.Scope

abstract class CurrentLocation {
    abstract fun getCurrentLocation(scope: Scope): Maybe<ResponseResult<Location>>
    abstract fun getLastReceivedLocation(): Coordinate?
}