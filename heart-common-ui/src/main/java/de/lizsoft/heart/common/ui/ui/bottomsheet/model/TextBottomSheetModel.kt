package de.lizsoft.heart.common.ui.ui.bottomsheet.model

import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import de.lizsoft.heart.common.ui.R
import de.lizsoft.heart.common.ui.ui.bottomsheet.TextBottomSheetDelegate
import java.io.Serializable

data class TextBottomSheetModel(
    val text: String,
    @StyleRes val textStyle: Int = R.style.AppTextAppearance_Body_Black,
    @DrawableRes val icon: Int = 0,
    val clickCallback: (TextBottomSheetDelegate) -> Unit
) : Serializable