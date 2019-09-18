package de.lizsoft.heart.deeplink

import android.content.Intent
import de.lizsoft.heart.interfaces.deeplink.FirebaseDeeplinkFetcher
import io.reactivex.Completable

class DeeplinkDispatcher(
      private val firebaseDeeplinkFetcher: FirebaseDeeplinkFetcher
) {

    fun dispatch(intent: Intent): Completable {
        return firebaseDeeplinkFetcher.getRoute(intent)
              .doOnSuccess { route ->
                  HeartDeepLink.heartDeepLinkDispatcher(route)
              }
              .ignoreElement()
    }
}