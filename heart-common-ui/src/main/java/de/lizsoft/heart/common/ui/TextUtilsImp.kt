package de.lizsoft.heart.common.ui

import android.content.Context
import androidx.annotation.StringRes
import de.lizsoft.heart.interfaces.common.TextUtils

class TextUtilsImp(private val context: Context) : TextUtils {

    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(@StringRes resId: Int, vararg formatArgs: String): String {
        return context.getString(resId, *formatArgs)
    }
}