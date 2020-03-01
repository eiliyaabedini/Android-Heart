package de.lizsoft.heart.errorhandler

class GenericErrorHandler(
      private val errorHandlerDispatcher: (Throwable) -> Unit
) {

    fun handleThrowable(throwable: Throwable) {
        throwable.printStackTrace()

        errorHandlerDispatcher(throwable)
    }
}
