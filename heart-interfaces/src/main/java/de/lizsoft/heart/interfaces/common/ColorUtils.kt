package de.lizsoft.heart.interfaces.common

import androidx.annotation.ColorRes

interface ColorUtils {

    fun getColor(@ColorRes resId: Int): Int
}