package de.lizsoft.heart.authentication

import de.lizsoft.heart.authentication.di.heartAuthenticationModule
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

object HeartAuthentication {

    internal lateinit var signedInScreenObject: Any
    internal lateinit var signInScreenObject: Any

    fun bind(
          signInScreenObject: Any,
          signedInScreenObject: Any,
          isTesting: Boolean = false
    ) {

        HeartAuthentication.signInScreenObject = signInScreenObject
        HeartAuthentication.signedInScreenObject = signedInScreenObject

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                if (isTesting.not() && BuildConfig.DEBUG) androidLogger(Level.DEBUG)
                heartAuthenticationModule
            }
        } else {
            loadKoinModules(
                  heartAuthenticationModule
            )
        }
    }
}