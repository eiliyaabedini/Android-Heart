package de.lizsoft.heart.common

import de.lizsoft.heart.common.event.DataEvent
import de.lizsoft.heart.common.event.EventManager
import de.lizsoft.heart.interfaces.common.BackendException

class GenericErrorHandler(
      private val eventManager: EventManager
) {

    fun handleThrowable(throwable: Throwable) {
        throwable.printStackTrace()
        if (throwable is BackendException) {
            if (throwable.isAuthenticationError()) {
                eventManager.notify(DataEvent.NotAuthenticated)
            }
        }
    }
}
