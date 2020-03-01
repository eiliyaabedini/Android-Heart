package de.lizsoft.travelcheck.error

class BackendException(vararg val backendErrors: BackendError) : Throwable() {

    fun isAuthenticationError(): Boolean {
        return backendErrors.any { it.type == "errorCode" && it.message == "unauthenticatedUser" }
    }
}
