package de.lizsoft.heart.common.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import de.lizsoft.heart.common.extension.dp2px
import de.lizsoft.heart.interfaces.common.DrawableUtils

class DrawableUtilsImp(private val context: Context) : DrawableUtils {

    override fun getBitmap(@DrawableRes resId: Int): Bitmap {
        val drawable = context.resources.getDrawable(resId, null)
        return makeBitmapFromDrawable(drawable.intrinsicWidth, drawable.intrinsicHeight, drawable)
    }

    override fun getBitmap(resId: Int, width: Int, height: Int): Bitmap {
        val drawable = context.resources.getDrawable(resId, null)
        return makeBitmapFromDrawable(width, height, drawable)
    }

    private fun makeBitmapFromDrawable(
        width: Int,
        height: Int,
        drawable: Drawable
    ): Bitmap {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)

        return bitmap
    }

    override fun getDrawable(resId: Int): Drawable {
        return context.resources.getDrawable(resId, null)
    }

    override fun mergeDrawableWithBitmap(bitmap: Bitmap, @DrawableRes drawable: Int, drawAtEnd: Boolean): Bitmap {

        val drawableBitmap = getBitmap(
              drawable, dp2px(16).toInt(), dp2px(16).toInt()
        )

        Canvas(bitmap).apply {
            drawBitmap(bitmap, 0f, 0f, null)
            if (drawAtEnd) {
                //Draw it at bottom right
                drawBitmap(
                      drawableBitmap,
                      (bitmap.width - drawableBitmap.width).toFloat(),
                      (bitmap.height - drawableBitmap.height).toFloat(),
                      null
                )
            } else {
                //Draw it at top left
                drawBitmap(drawableBitmap, 0f, 0f, null)
            }
        }

        return bitmap
    }
}