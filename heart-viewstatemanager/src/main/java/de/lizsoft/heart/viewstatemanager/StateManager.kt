package de.lizsoft.heart.viewstatemanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout

class StateManager(
    private val context: Context,
    private val contentLayout: FrameLayout,
    private val overlayLayout: FrameLayout,
    private val normalLayout: Int,
    private val loadingDefaultLayout: Int,
    private val errorDefaultLayout: Int,
    private val errorRetryButtonDefaultLayout: Int,
    private val loadingLayoutId: Int?,
    private val errorLayoutId: Int?,
    private val errorRetryButtonId: Int?,
    private val onErrorRetryCallback: () -> Unit = {}
) {

    private var currentViewState: ViewState = ViewState.NONE

    fun changeState(viewState: ViewState) {

        if (currentViewState == viewState) return
        currentViewState = viewState

        when (viewState) {
            is ViewState.Normal -> {
                resetEverything()

                if (normalLayout != -1 && contentLayout.childCount == 0) {
                    contentLayout.removeAllViews()
                    LayoutInflater.from(context).inflate(normalLayout, contentLayout, true)
                }
            }

            is ViewState.Loading -> {
                resetEverything()

                if (loadingLayoutId != null) {
                    contentLayout.findViewById<View>(loadingLayoutId).visibility = View.VISIBLE
                } else {
                    LayoutInflater.from(context).inflate(loadingDefaultLayout, overlayLayout, true)
                }
            }

            is ViewState.Error -> {
                resetEverything()
                if (errorLayoutId != null) {
                    contentLayout.findViewById<View>(errorLayoutId).visibility = View.VISIBLE

                    errorRetryButtonId?.let { buttonId ->
                        contentLayout.findViewById<View>(buttonId)
                              .setOnClickListener {
                                  onErrorRetryCallback()
                              }
                    }
                } else {
                    val view = LayoutInflater.from(context).inflate(errorDefaultLayout, overlayLayout, true)
                    view.findViewById<View>(errorRetryButtonDefaultLayout)
                          .setOnClickListener {
                              onErrorRetryCallback()
                          }
                }

            }
        }
    }

    private fun resetEverything() {
        overlayLayout.removeAllViews()

        if (loadingLayoutId != null) {
            contentLayout.findViewById<View>(loadingLayoutId)?.visibility = View.GONE
        }

        if (errorLayoutId != null) {
            contentLayout.findViewById<View>(errorLayoutId)?.visibility = View.GONE
        }
    }
}
