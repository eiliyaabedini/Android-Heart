package de.lizsoft.heart.interfaces.map.service

import de.lizsoft.heart.interfaces.ResponseResult
import de.lizsoft.heart.interfaces.map.Location
import io.reactivex.Single
import org.koin.core.scope.Scope

interface AddressService {

    fun getSuggestionsResult(
        locationName: String,
        scope: Scope
    ): Single<ResponseResult<List<Location>>>
}