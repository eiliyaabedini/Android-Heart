package de.lizsoft.heart.interfaces.koin

import android.content.ComponentCallbacks
import org.koin.android.ext.android.getKoin
import org.koin.core.KoinComponent
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

object Qualifiers {
    val authenticatedUser: Qualifier = named("authenticatedUser")
    val baseApiUrl: Qualifier = named("baseApiUrl")

    val noCachingApiOKHTTP: Qualifier = named("noCachingApiOKHTTP")
    val cachingApiOKHTTP: Qualifier = named("cachingApiOKHTTP")
    val noCachingApiRETROFIT: Qualifier = named("noCachingApiRETROFIT")
    val cachingApiRETROFIT: Qualifier = named("cachingApiRETROFIT")

    val applicationContext: Qualifier = named("ApplicationContext")
    val applicationInstance: Qualifier = named("applicationInstance")
}

fun ComponentCallbacks.getAuthenticatedUserScope() : Scope =
      getKoin().getOrCreateScope(Qualifiers.authenticatedUser.toString(), Qualifiers.authenticatedUser)

fun KoinComponent.getAuthenticatedUserScope() : Scope =
      getKoin().getOrCreateScope(Qualifiers.authenticatedUser.toString(), Qualifiers.authenticatedUser)

fun Scope.getAuthenticatedUserScope() : Scope =
      getKoin().getOrCreateScope(Qualifiers.authenticatedUser.toString(), Qualifiers.authenticatedUser)
