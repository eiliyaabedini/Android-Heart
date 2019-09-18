package de.lizsoft.heart.interfaces.common.ui

import android.content.Intent

data class ActivityResult(
      val requestCode: Int,
      val data: Intent?
) {
    companion object {
        val EMPTY = ActivityResult(requestCode = -100, data = null)
    }
}