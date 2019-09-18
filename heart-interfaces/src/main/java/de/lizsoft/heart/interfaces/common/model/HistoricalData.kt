package de.lizsoft.heart.interfaces.common.model

data class HistoricalData<T>(
    val model: T,
    val timestamp: Long
)