package de.lizsoft.heart.testhelper

import timber.log.Timber

class TestDebugTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        val internalTag = tag ?: "NoTag"

        logToConsole(internalTag, message)

        if (throwable?.message != null) {
            logToConsole(internalTag, throwable.message!!)
        }
    }

    private fun logToConsole(tag: String, message: String) {
        println("tag:$tag -> $message")
    }
}