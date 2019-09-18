package de.lizsoft.heart.pushnotification.di

import de.lizsoft.heart.interfaces.koin.Qualifiers
import de.lizsoft.heart.interfaces.pushnotification.InternalPushNotificationManager
import de.lizsoft.heart.pushnotification.InternalPushNotificationManagerImp
import org.koin.dsl.module

val heartPushNotificationModule = module {
    scope(Qualifiers.authenticatedUser) {
        scoped<InternalPushNotificationManager> { InternalPushNotificationManagerImp(get(), get()) }
    }
}
