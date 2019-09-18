package de.lizsoft.heart.common.ui.glide

import android.graphics.*
import androidx.annotation.ColorInt
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class CropCircleWithBorderTransformation(
    private val borderWidth: Float,
    @ColorInt private val borderColor: Int
) : BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("CropCircleWithBorderTransformation".toByteArray())
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {

        val width: Int = outWidth
        val height: Int = outHeight

        val scaledImage: Bitmap = Bitmap.createScaledBitmap(toTransform, outWidth, outHeight, false)

        val bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setHasAlpha(true)

        val radius = Math.min(width, height).toFloat()

        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth
        paint.color = borderColor
        canvas.drawRoundRect(
              RectF(borderWidth / 2, borderWidth / 2, width.toFloat() - borderWidth, height.toFloat() - borderWidth),
              radius, radius, paint
        )

        paint.style = Paint.Style.FILL
        paint.shader = BitmapShader(scaledImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawRoundRect(
              RectF(borderWidth / 2, borderWidth / 2, width.toFloat() - borderWidth, height.toFloat() - borderWidth),
              radius, radius, paint
        )

        return bitmap
    }
}