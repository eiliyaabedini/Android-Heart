package de.lizsoft.heart.common.di

import com.google.gson.GsonBuilder
import de.lizsoft.heart.common.event.EventManager
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.experimental.builder.single

val heartCommonModule: Module = module {
    single<EventManager>()

    factory {
        GsonBuilder()
              .create()
    }
}
