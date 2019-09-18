package de.lizsoft.heart.interfaces.common

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

interface DrawableUtils {

    fun getBitmap(@DrawableRes resId: Int): Bitmap

    fun getBitmap(@DrawableRes resId: Int, width: Int, height: Int): Bitmap

    fun getDrawable(@DrawableRes resId: Int): Drawable

    fun mergeDrawableWithBitmap(bitmap: Bitmap, @DrawableRes drawable: Int, drawAtEnd: Boolean = false): Bitmap
}