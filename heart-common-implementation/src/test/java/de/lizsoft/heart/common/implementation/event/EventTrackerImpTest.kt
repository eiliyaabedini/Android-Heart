package de.lizsoft.heart.common.implementation.event

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import de.lizsoft.heart.interfaces.common.event.FirebaseAnalyticsLogger
import org.junit.Test

class EventTrackerImpTest {

    private val firebaseAnalyticsLogger: FirebaseAnalyticsLogger = mock()
    private val eventTracker: EventTrackerImp =
          EventTrackerImp(
                firebaseAnalyticsLogger = firebaseAnalyticsLogger
          )

    @Test
    fun `when adding arguments then log events with arguments`() {
        eventTracker.make("name1")
              .add("a", "av")
              .add("b", "bv")
              .add("c", "cv")
              .add("d", "dv")
              .build()
              .track()

        verify(firebaseAnalyticsLogger).track(
              "name1", mutableMapOf(
              "a" to "av",
              "b" to "bv",
              "c" to "cv",
              "d" to "dv"
        )
        )
    }

    @Test
    fun `when adding no arguments then log events without arguments`() {
        eventTracker.make("name2")
              .build()
              .track()

        verify(firebaseAnalyticsLogger).track("name2", mutableMapOf())
    }
}