package de.lizsoft.heart.interfaces.common

import de.lizsoft.heart.interfaces.common.model.BackendError

class BackendException(vararg val backendErrors: BackendError) : Throwable() {

    fun isAuthenticationError(): Boolean {
        return backendErrors.any { it.type == "errorCode" && it.message == "unauthenticatedUser" }
    }
}