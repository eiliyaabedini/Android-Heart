package de.lizsoft.heart.common.ui.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.google.android.material.button.MaterialButton
import de.lizsoft.heart.common.extension.dp2px
import de.lizsoft.heart.common.ui.R
import io.reactivex.disposables.CompositeDisposable

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val button: MaterialButton by lazy {
        MaterialButton(context)
    }

    private val progressBar: ProgressBar by lazy {
        ProgressBar(context, null, android.R.attr.progressBarStyle).apply {
            elevation = dp2px(20)
            indeterminateTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey))
            isGone = true
        }
    }

    private val disposables = CompositeDisposable()
    private var shouldShowProgressBar = true

    @StringRes private var buttonTextRes: Int = 0
    private lateinit var currentStyleType: LoadingButtonType

    fun bind(
          @StringRes buttonText: Int,
          type: LoadingButtonType,
          @ColorRes textColor: Int = 0,
          isEnabled: Boolean = true
    ) {

        buttonTextRes = buttonText

        hideLoading()

        updateStyle(
              type,
              textColor,
              isEnabled
        )

        if (type != LoadingButtonType.NONE) {

            button.setText(buttonTextRes)

            if (button.parent == null) {
                button.isAllCaps = false
                button.cornerRadius = BUTTON_HEIGHT
                button.setOnClickListener {
                    showLoading()
                }

                addView(
                      button,
                      FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            BUTTON_HEIGHT
                      )
                )
            }

            if (progressBar.parent == null) {
                addView(
                      progressBar,
                      FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            PROGRESS_BAR_HEIGHT
                      ).apply {
                          gravity = Gravity.CENTER
                      }
                )
            }
        }
    }

    fun showLoading() {
        if (shouldShowProgressBar) {
            progressBar.isGone = false
            button.text = ""
        }
    }

    fun showSuccess() {
        if (progressBar.isShown) {
            hideLoading()
        }

        updateStyle(LoadingButtonType.SUCCESS)
    }

    fun hideLoading() {
        progressBar.isGone = true

        if (buttonTextRes == 0) {
            button.text = ""
        } else {
            button.setText(buttonTextRes)
        }
    }

    private fun updateStyle(
          type: LoadingButtonType,
          @ColorRes textColor: Int = R.color.black,
          isEnabled: Boolean = true
    ) {
        currentStyleType = type
        isGone = currentStyleType == LoadingButtonType.NONE
        setButtonEnabled(isEnabled)
        button.stateListAnimator = null

        when (currentStyleType) {
            LoadingButtonType.NONE -> {
                //We do nothing here
            }

            LoadingButtonType.SUCCESS -> {
                button.alpha = 1.0F
                button.setTextColor(ContextCompat.getColor(context, R.color.success))

                button.backgroundTintList = null

                button.setCompoundDrawablesRelativeWithIntrinsicBounds(
                      null,
                      null,
                      ContextCompat.getDrawable(context, R.drawable.check_success_with_circle_border),
                      null
                )
                button.setPaddingRelative(
                      BUTTON_SIDE_PADDING,
                      BUTTON_VERTICAL_PADDING,
                      BUTTON_SIDE_PADDING,
                      BUTTON_VERTICAL_PADDING
                )
                button.gravity = Gravity.CENTER
            }

            LoadingButtonType.PRIMARY -> {

                button.setTextColor(ContextCompat.getColor(context, R.color.white))
                button.backgroundTintList = ContextCompat.getColorStateList(
                      context,
                      R.color.mtrl_btn_bg_primary_color_selector
                )

                button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                button.setPaddingRelative(
                      BUTTON_SIDE_PADDING,
                      BUTTON_VERTICAL_PADDING,
                      BUTTON_SIDE_PADDING,
                      BUTTON_VERTICAL_PADDING
                )
                button.gravity = Gravity.CENTER
            }

            LoadingButtonType.SECONDARY -> {

                button.setTextColor(ContextCompat.getColor(context, R.color.grey))
                button.backgroundTintList = ContextCompat.getColorStateList(
                      context,
                      R.color.mtrl_btn_bg_secondary_color_selector
                )

                button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                button.setPaddingRelative(
                      BUTTON_SIDE_PADDING,
                      BUTTON_VERTICAL_PADDING,
                      BUTTON_SIDE_PADDING,
                      BUTTON_VERTICAL_PADDING
                )
                button.gravity = Gravity.CENTER
            }

            LoadingButtonType.TEXT -> {
                button.setTextColor(ContextCompat.getColor(context, textColor))
                button.backgroundTintList = ContextCompat.getColorStateList(
                      context,
                      R.color.transparent
                )
                button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                button.setPaddingRelative(0,
                      BUTTON_VERTICAL_PADDING, 0,
                      BUTTON_VERTICAL_PADDING
                )
                button.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            }
        }
    }

    fun setEndAligned() {
        button.gravity = Gravity.END or Gravity.CENTER_VERTICAL
    }

    fun setShouldShowProgressBar(shouldShowProgressBar: Boolean) {
        this.shouldShowProgressBar = shouldShowProgressBar
    }

    fun setButtonEnabled(isEnabled: Boolean) {
        button.isEnabled = isEnabled
        if (isEnabled) {
            button.alpha = 1.0F
        } else {
            button.alpha = 0.5F
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        button.setOnClickListener { view ->
            if (!progressBar.isShown && button.isEnabled) {
                showLoading()
                l?.onClick(view)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        disposables.clear()
    }

    enum class LoadingButtonType {
        NONE,
        PRIMARY,
        SECONDARY,
        SUCCESS,
        TEXT
    }

    companion object {
        private val BUTTON_HEIGHT: Int = dp2px(48).toInt()
        private val BUTTON_SIDE_PADDING: Int = dp2px(16).toInt()
        private val BUTTON_VERTICAL_PADDING: Int = dp2px(10).toInt()
        private val PROGRESS_BAR_HEIGHT: Int = dp2px(32).toInt()
    }
}