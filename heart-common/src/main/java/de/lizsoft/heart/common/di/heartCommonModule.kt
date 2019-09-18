package de.lizsoft.heart.common.di

import com.google.gson.GsonBuilder
import de.lizsoft.heart.common.GenericErrorHandler
import de.lizsoft.heart.common.event.EventManager
import org.koin.core.module.Module
import org.koin.dsl.module

val heartCommonModule: Module = module {
    single { EventManager() }

    single { GenericErrorHandler(get()) }

    factory {
        GsonBuilder()
              .create()
    }
}