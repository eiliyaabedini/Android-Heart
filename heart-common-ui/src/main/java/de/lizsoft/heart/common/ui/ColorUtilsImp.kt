package de.lizsoft.heart.common.ui

import android.content.Context
import androidx.core.content.ContextCompat
import de.lizsoft.heart.interfaces.common.ColorUtils

class ColorUtilsImp(private val context: Context) : ColorUtils {

    override fun getColor(resId: Int): Int {
        return ContextCompat.getColor(context, resId)
    }
}