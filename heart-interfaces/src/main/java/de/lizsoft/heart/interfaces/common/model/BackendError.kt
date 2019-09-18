package de.lizsoft.heart.interfaces.common.model

import java.io.Serializable

data class BackendError(
    val type: String,
    val message: String
) : Serializable