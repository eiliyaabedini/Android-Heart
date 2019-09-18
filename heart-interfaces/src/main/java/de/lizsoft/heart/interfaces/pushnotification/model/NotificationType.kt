package de.lizsoft.heart.interfaces.pushnotification.model

import androidx.annotation.DrawableRes

class NotificationType(
      val notificationKey: String,
      val notificationTitle: String?,
      val notificationBody: String,
      val alerterColorType: AlerterColorType,
      @DrawableRes val notificationIconRes: Int?,
      val notificationRideId: String
)