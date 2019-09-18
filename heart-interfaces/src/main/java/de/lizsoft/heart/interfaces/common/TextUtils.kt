package de.lizsoft.heart.interfaces.common

import androidx.annotation.StringRes

interface TextUtils {

    fun getString(@StringRes resId: Int): String

    fun getString(@StringRes resId: Int, vararg formatArgs: String): String
}