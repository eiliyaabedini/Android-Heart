package de.lizsoft.heart.maptools

import de.lizsoft.heart.maptools.di.heartMapUtilsModule
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

object HeartMap {

    fun bind(googleMapApiKey: String) {

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                heartMapUtilsModule(googleMapApiKey)
            }
        } else {
            loadKoinModules(
                  heartMapUtilsModule(googleMapApiKey)
            )
        }
    }
}