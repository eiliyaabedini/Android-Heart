package de.lizsoft.heart.pushnotification

import de.lizsoft.heart.pushnotification.di.heartPushNotificationModule
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

object HeartPushNotification {

    internal lateinit var tokenRenewed: (String) -> Unit
    internal lateinit var shouldReceivePushNotification: () -> Boolean
    internal val receivedPushNotificationKeysInData = mutableListOf<String>()

    fun bind(
          listOfKeys: List<String>,
          shouldReceivePushNotification: () -> Boolean,
          tokenRenewed : (String) -> Unit,
          isTesting: Boolean = false
    ) {

        receivedPushNotificationKeysInData.addAll(listOfKeys)
        HeartPushNotification.shouldReceivePushNotification = shouldReceivePushNotification
        HeartPushNotification.tokenRenewed = tokenRenewed

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                if (isTesting.not() && BuildConfig.DEBUG) androidLogger(Level.DEBUG)
                heartPushNotificationModule
            }
        } else {
            loadKoinModules(
                  heartPushNotificationModule
            )
        }
    }
}