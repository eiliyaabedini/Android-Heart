package de.lizsoft.heart.deeplink

import android.content.Intent
import de.lizsoft.heart.interfaces.deeplink.FirebaseDeeplinkFetcher
import de.lizsoft.heart.interfaces.deeplink.model.hasValue
import io.reactivex.Single

class DeeplinkDispatcher(
      private val firebaseDeeplinkFetcher: FirebaseDeeplinkFetcher
) {

    fun dispatch(intent: Intent): Single<Boolean> {
        return firebaseDeeplinkFetcher.getRoute(intent)
              .doOnSuccess { route ->
                  HeartDeepLink.heartDeepLinkDispatcher(route)
              }
              .map { route ->
                  route.hasValue()
              }
    }
}