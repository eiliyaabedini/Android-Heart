package de.lizsoft.heart.interfaces.common.model

data class HistoricalSavedData(
    val modelString: String,
    val timestamp: Long = System.currentTimeMillis()
)