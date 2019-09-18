package de.lizsoft.heart.interfaces.common.event

interface FirebaseAnalyticsLogger {
    fun track(
        name: String,
        params: Map<String, String>
    )
}