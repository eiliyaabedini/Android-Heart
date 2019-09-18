package de.lizsoft.heart.interfaces.common.event

interface EventTracker {
    fun make(name: String): EventTrackerBuilder
}