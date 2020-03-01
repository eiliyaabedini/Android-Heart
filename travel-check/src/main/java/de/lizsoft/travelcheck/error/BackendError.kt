package de.lizsoft.travelcheck.error

import java.io.Serializable

data class BackendError(
    val type: String,
    val message: String
) : Serializable
