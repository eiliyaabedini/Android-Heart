package de.lizsoft.heart.common.event

interface DataEvent {

    object NotAuthenticated : DataEvent

    //We use them to show notification badge in home screen
    data class UnreadNotificationsCountChanged(val count: Int) : DataEvent

    object NotificationsSeen : DataEvent

    data class NotificationReceived(
          val key: String,
          val title: String?,
          val message: String?,
          val extraValues: Map<String, String?>
    ) : DataEvent

}


