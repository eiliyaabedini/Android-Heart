package de.lizsoft.heart.interfaces.common.event

interface EventTrackerBuilder {
    fun add(key: String, value: String): EventTrackerBuilder
    fun build(): EventTrackerFinalized
}