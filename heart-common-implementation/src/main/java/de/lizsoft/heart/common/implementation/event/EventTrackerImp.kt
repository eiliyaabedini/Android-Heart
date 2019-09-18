package de.lizsoft.heart.common.implementation.event

import de.lizsoft.heart.interfaces.common.event.EventTracker
import de.lizsoft.heart.interfaces.common.event.EventTrackerBuilder
import de.lizsoft.heart.interfaces.common.event.EventTrackerFinalized
import de.lizsoft.heart.interfaces.common.event.FirebaseAnalyticsLogger

class EventTrackerImp(
    private val firebaseAnalyticsLogger: FirebaseAnalyticsLogger
) : EventTracker {

    override fun make(name: String): EventTrackerBuilder {
        return EventTrackerBuilderImp(
              firebaseAnalyticsLogger = firebaseAnalyticsLogger,
              name = name
        )
    }

    class EventTrackerBuilderImp(
          private val firebaseAnalyticsLogger: FirebaseAnalyticsLogger,
          private val name: String
    ) : EventTrackerBuilder {

        private val params = mutableMapOf<String, String>()

        override fun add(key: String, value: String): EventTrackerBuilder {
            params.put(key, value)
            return this
        }

        override fun build(): EventTrackerFinalized {
            return EventTrackerFinalizedImp(
                  firebaseAnalyticsLogger = firebaseAnalyticsLogger,
                  params = params,
                  name = name
            )
        }
    }

    class EventTrackerFinalizedImp(
          private val firebaseAnalyticsLogger: FirebaseAnalyticsLogger,
          private val params: Map<String, String>,
          private val name: String
    ) : EventTrackerFinalized {

        override fun track() {
            firebaseAnalyticsLogger.track(name, params)
        }
    }
}