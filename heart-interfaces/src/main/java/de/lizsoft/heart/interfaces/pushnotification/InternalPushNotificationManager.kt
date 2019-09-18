package de.lizsoft.heart.interfaces.pushnotification

import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import de.lizsoft.heart.interfaces.pushnotification.model.NotificationType

interface InternalPushNotificationManager {

    fun showInternalPushNotification(
          notificationType: NotificationType,
          clickCallback: (ActivityWithPresenterInterface, InternalAlerter) -> Unit
    )
}