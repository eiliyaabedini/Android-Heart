package de.lizsoft.heart.pushnotification

import de.lizsoft.heart.common.ui.extension.showInternalAlertForNotifications
import de.lizsoft.heart.interfaces.common.ForegroundActivityService
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.get
import de.lizsoft.heart.interfaces.common.isSome
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import de.lizsoft.heart.interfaces.pushnotification.InternalAlerter
import de.lizsoft.heart.interfaces.pushnotification.InternalPushNotificationManager
import de.lizsoft.heart.interfaces.pushnotification.model.NotificationType

class InternalPushNotificationManagerImp(
      private val reactiveTransformer: ReactiveTransformer,
      private val foregroundActivityService: ForegroundActivityService
) : InternalPushNotificationManager {

    override fun showInternalPushNotification(
          notificationType: NotificationType,
          clickCallback: (ActivityWithPresenterInterface, InternalAlerter) -> Unit
    ) {
        foregroundActivityService.resumed
              .subscribeOn(reactiveTransformer.ioScheduler())
              .filter { it.isSome }
              .map { it.get() }
              .firstElement()
              .observeOn(reactiveTransformer.mainThreadScheduler())
              .subscribe { activity ->
                  activity.showInternalAlertForNotifications(notificationType, clickCallback)
              }
    }
}