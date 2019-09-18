package de.lizsoft.heart.common.ui.ui.bottomsheet

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TextBottomSheetDelegateImp(
    val dialog: BottomSheetDialogFragment
) : TextBottomSheetDelegate {

    override fun dismiss() {
        dialog.dismiss()
    }
}