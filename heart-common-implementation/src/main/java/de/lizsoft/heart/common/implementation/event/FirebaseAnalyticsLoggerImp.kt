package de.lizsoft.heart.common.implementation.event

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import de.lizsoft.heart.interfaces.common.event.FirebaseAnalyticsLogger

class FirebaseAnalyticsLoggerImp(
    private val firebaseAnalytics: FirebaseAnalytics
): FirebaseAnalyticsLogger {

    override fun track(
        name: String,
        params: Map<String, String>
    ) {
        val bundle : Bundle = Bundle().apply {
            params.forEach { (key, value) ->
                putString(key, value)
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }
}