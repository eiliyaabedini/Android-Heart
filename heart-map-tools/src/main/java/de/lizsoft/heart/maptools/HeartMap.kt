package de.lizsoft.heart.maptools

import de.lizsoft.heart.maptools.di.heartMapUtilsModule
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

object HeartMap {

    fun bind() {

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                heartMapUtilsModule
            }
        } else {
            loadKoinModules(
                  heartMapUtilsModule
            )
        }
    }
}