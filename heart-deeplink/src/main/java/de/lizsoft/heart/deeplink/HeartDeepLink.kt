package de.lizsoft.heart.deeplink

import de.lizsoft.heart.deeplink.di.heartDeeplinkModule
import de.lizsoft.heart.interfaces.deeplink.model.Route
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

object HeartDeepLink {

    internal lateinit var heartDeepLinkDispatcher: (Route) -> Unit

    fun bind(isTesting: Boolean = false, dispatcher: (Route) -> Unit) {

        heartDeepLinkDispatcher = dispatcher

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                if (isTesting.not() && BuildConfig.DEBUG) androidLogger(Level.DEBUG)
                heartDeeplinkModule
            }
        } else {
            loadKoinModules(
                  heartDeeplinkModule
            )
        }
    }
}