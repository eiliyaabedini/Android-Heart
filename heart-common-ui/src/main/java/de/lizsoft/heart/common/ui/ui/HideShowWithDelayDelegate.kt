package de.lizsoft.heart.common.ui.ui

import android.os.Handler
import android.os.Looper

/**
 * We'v mostly copy-pasted ContentLoadingProgressBar class into this delegate class.
 */
class HideShowWithDelayDelegate {


    private lateinit var hideMethod: () -> Unit
    private lateinit var showMethod: () -> Unit
    private var startTime: Long = -1
    private var postedHide = false
    private var postedShow = false
    private var dismissed = false
    private val handler: Handler = Handler(Looper.getMainLooper())

    private var mDelayedHide = Runnable {
        postedHide = false
        startTime = -1
        hideMethod.invoke()
    }

    private var mDelayedShow = Runnable {
        postedShow = false
        if (!dismissed) {
            startTime = System.currentTimeMillis()
            showMethod.invoke()
        }
    }

    private fun removeCallbacks() {
        handler.removeCallbacks(mDelayedHide)
        handler.removeCallbacks(mDelayedShow)
    }

    private fun resetState() {
        postedHide = false
        postedShow = false
        dismissed = false
        startTime = -1
    }

    fun attach() {
        removeCallbacks()
        resetState()
    }

    fun detach() {
        removeCallbacks()
        resetState()
    }

    /**
     * Hide the progress view if it is visible. The progress view will not be
     * hidden until it has been shown for at least a minimum show time. If the
     * progress view was not yet visible, cancels showing the progress view.
     */
    fun hideProgress(hideMethod: () -> Unit) {
        dismissed = true
        handler.removeCallbacks(mDelayedShow)
        postedShow = false
        val diff = System.currentTimeMillis() - startTime
        if (diff >= MIN_SHOW_TIME || startTime == -1L) {
            // The progress spinner has been shown long enough
            // OR was not shown yet. If it wasn't shown yet,
            // it will just never be shown.
            hideMethod.invoke()
        } else {
            // The progress spinner is shown, but not long enough,
            // so put a delayed message in to hide it when its been
            // shown long enough.
            this.hideMethod = hideMethod
            if (!postedHide) {
                handler.postDelayed(mDelayedHide, MIN_SHOW_TIME - diff)
                postedHide = true
            }
        }
    }

    /**
     * Show the progress view after waiting for a minimum delay. If
     * during that time, hide() is called, the view is never made visible.
     */
    fun showProgress(showMethod: () -> Unit) {
        // Reset the start time.
        startTime = -1
        dismissed = false
        handler.removeCallbacks(mDelayedHide)
        postedHide = false
        this.showMethod = showMethod
        if (!postedShow) {
            handler.postDelayed(mDelayedShow, MIN_DELAY)
            postedShow = true
        }
    }

    companion object {
        private const val MIN_SHOW_TIME = 1500L // ms
        private const val MIN_DELAY = 500L // ms
    }
}