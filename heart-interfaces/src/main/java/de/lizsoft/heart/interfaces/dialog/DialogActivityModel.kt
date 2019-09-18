package de.lizsoft.heart.interfaces.dialog

import androidx.annotation.DrawableRes
import java.io.Serializable

data class DialogActivityModel(
    @DrawableRes val imageDrawable: Int,
    val title: String,
    val description: String,
    val secondDescription: String
) : Serializable