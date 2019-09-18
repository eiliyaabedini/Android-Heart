package de.lizsoft.heart.interfaces.common.ui

import org.koin.core.scope.Scope

data class ScreenBucketModel(
    val scope: Scope,
    val viewLayout: Int = -1,
    val enableDisplayHomeAsUp: Boolean = false,
    val hideSystemBar: Boolean = false,
    val loadingLayout: Int? = null,
    val errorLayout: Int? = null,
    val errorRetryButtonId: Int? = null
)