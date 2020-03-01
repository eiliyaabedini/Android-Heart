package de.lizsoft.heart.pushnotification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import de.lizsoft.heart.common.event.DataEvent
import de.lizsoft.heart.common.event.EventManager
import org.koin.android.ext.android.inject
import timber.log.Timber

class HeartFirebaseMessagingService : FirebaseMessagingService() {

    private val eventManager: EventManager by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d(
              "From:%s, Data:%s",
              remoteMessage.from,
              remoteMessage.data.keys.joinToString()
        )

        if (!HeartPushNotification.shouldReceivePushNotification()) {
            Timber.e("Received push notification ignored.")
            return
        }

        eventManager.notify(
              DataEvent.NotificationReceived(
                    key = remoteMessage.data["key"]!!,
                    title = remoteMessage.data["title"],
                    message = remoteMessage.data["message"],
                    extraValues = HeartPushNotification.receivedPushNotificationKeysInData
                          .map { key -> key to remoteMessage.data[key] }
                          .toMap()
              )
        )
    }

    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: %s", token)
        HeartPushNotification.tokenRenewed(token)
    }
}
