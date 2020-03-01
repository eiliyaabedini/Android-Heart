package de.lizsoft.heart.errorhandler

import de.lizsoft.heart.errorhandler.di.heartErrorHandlerModule
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

object HeartErrorHandler {

    fun bind(isTesting: Boolean = false, dispatcher: (Throwable) -> Unit) {

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                if (isTesting) androidLogger(Level.DEBUG)
                heartErrorHandlerModule(dispatcher)
            }
        } else {
            loadKoinModules(
                  heartErrorHandlerModule(dispatcher)
            )
        }
    }
}
