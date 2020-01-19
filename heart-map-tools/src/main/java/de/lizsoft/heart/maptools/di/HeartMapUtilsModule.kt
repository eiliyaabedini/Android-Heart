package de.lizsoft.heart.maptools.di

import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import de.lizsoft.heart.interfaces.koin.Qualifiers
import de.lizsoft.heart.interfaces.map.service.AddressService
import de.lizsoft.heart.interfaces.map.service.CurrentLocation
import de.lizsoft.heart.interfaces.map.service.LocationFulfiller
import de.lizsoft.heart.interfaces.map.service.PlacesService
import de.lizsoft.heart.maptools.BuildConfig
import de.lizsoft.heart.maptools.services.CurrentLocationImp
import de.lizsoft.heart.maptools.services.LocationFulfillerImp
import de.lizsoft.heart.maptools.services.PlacesServiceImp
import org.koin.core.module.Module
import org.koin.dsl.module

fun heartMapUtilsModule(googleMapApiKey: String): Module = module {

    factory<CurrentLocation> {
        CurrentLocationImp(
              get(Qualifiers.applicationContext),
              get(),
              get(),
              get(),
              get()
        )
    }

    single {
        Places.initialize(get(Qualifiers.applicationContext), googleMapApiKey)
        Places.createClient(get(Qualifiers.applicationContext))
    }

    single {
        AutocompleteSessionToken.newInstance()
    }

    factory<PlacesService> { PlacesServiceImp(get(), get()) }

    factory<LocationFulfiller> { LocationFulfillerImp(get(Qualifiers.applicationContext)) }

    factory<AddressService> { de.lizsoft.heart.maptools.services.AddressServiceImp(get(), get()) }
}