package de.lizsoft.heart.common.ui.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetBehaviorWithHalfExpansion<V : View> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BottomSheetBehavior<V>(context, attrs) {

    private val unknownOffset = -1.0F
    private val collapsedOffset = 0F
    private val halfExpandedOffset = 0.5F
    private val expandedOffset = 1F
    private var customState: Int = BottomSheetBehavior.STATE_HIDDEN

    var initialOffset: Float = unknownOffset
    var currentOffset: Float = unknownOffset
    var changeListener: BottomSheetBehaviorChangeListener? = null

    init {
        enableHalfExpandedState()
    }

    override fun setBottomSheetCallback(callback: BottomSheetCallback?) {
        //We do not call super function here just to prevent ignoring our logic here
    }

    private fun enableHalfExpandedState() {
        super.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    val difference = initialOffset - currentOffset
                    if (difference > 0) { // Collapsing
                        if (currentOffset < halfExpandedOffset) {
                            customState = BottomSheetBehavior.STATE_COLLAPSED
                        } else if (currentOffset < expandedOffset) {
                            customState = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }
                    } else { // Expanding
                        if (currentOffset > halfExpandedOffset) {
                            customState = BottomSheetBehavior.STATE_EXPANDED
                        } else if (currentOffset > collapsedOffset) {
                            customState = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }
                    }
                    state = customState
                    changeListener?.onStateChanged(customState)
                } else if (newState != BottomSheetBehavior.STATE_DRAGGING) {
                    initialOffset = unknownOffset
                    // avoid setting any other state to the bottom sheet by the system
                    state = customState
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (initialOffset == unknownOffset) {
                    initialOffset = slideOffset
                }
                currentOffset = slideOffset

            }
        })
    }
}