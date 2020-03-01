package de.lizsoft.heart.errorhandler.di

import de.lizsoft.heart.errorhandler.GenericErrorHandler
import org.koin.core.module.Module
import org.koin.dsl.module

fun heartErrorHandlerModule(
      errorHandlerDispatcher: (Throwable) -> Unit
): Module = module {

    single {
        GenericErrorHandler(errorHandlerDispatcher)
    }
}
