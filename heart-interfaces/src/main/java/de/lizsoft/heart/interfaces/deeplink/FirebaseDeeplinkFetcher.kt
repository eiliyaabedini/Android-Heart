package de.lizsoft.heart.interfaces.deeplink

import android.content.Intent
import de.lizsoft.heart.interfaces.deeplink.model.Route
import io.reactivex.Single

interface FirebaseDeeplinkFetcher {
    fun getRoute(intent: Intent): Single<Route>
}