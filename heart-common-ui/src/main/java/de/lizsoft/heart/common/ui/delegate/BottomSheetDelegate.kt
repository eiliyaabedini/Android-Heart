package de.lizsoft.heart.common.ui.delegate

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.lizsoft.heart.common.ui.ActivityWithPresenter
import de.lizsoft.heart.common.ui.extension.disableDragging

class BottomSheetDelegate(
      activity: ActivityWithPresenter,
      viewBottomSheet: View,
      private val viewBottomSheetBackground: View
) {

    init {
        viewBottomSheet.isClickable = true

        activity.onBackPressedDispatcher.addCallback(activity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isExpanded()) {
                    collapse()
                }
            }
        })
    }

    private val bottomSheetBehavior: BottomSheetBehavior<View> by lazy {
        BottomSheetBehavior.from<View>(viewBottomSheet).apply {
            disableDragging()
        }
    }

    fun isExpanded(): Boolean {
        return bottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED
    }

    fun expand() {
        viewBottomSheetBackground.isGone = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun collapse() {
        viewBottomSheetBackground.isGone = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}