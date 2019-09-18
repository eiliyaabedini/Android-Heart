package de.lizsoft.heart.common.ui.ui.bottomsheet

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.lizsoft.heart.common.extension.dp2px
import de.lizsoft.heart.common.extension.isNotEmpty
import de.lizsoft.heart.common.ui.R
import de.lizsoft.heart.common.ui.extension.addRippleEffect
import de.lizsoft.heart.common.ui.extension.bindView
import de.lizsoft.heart.common.ui.extension.setTextOrGone
import de.lizsoft.heart.common.ui.ui.bottomsheet.model.TextBottomSheetModel

class TextBottomSheet private constructor() : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.common_text_bottom_sheet, container, false)

    private val receivedTexts: List<TextBottomSheetModel> by lazy {
        (arguments?.getSerializable(ARGUMENT_TEXTS_KEY) as? Array<TextBottomSheetModel>)?.toList() ?: emptyList()
    }

    private val receivedTitle: String? by lazy {
        arguments?.getString(ARGUMENT_TITLE_KEY)
    }

    private val receivedDescription: String? by lazy {
        arguments?.getString(ARGUMENT_DESCRIPTION_KEY)
    }

    private val delegate: TextBottomSheetDelegate by lazy {
        TextBottomSheetDelegateImp(this)
    }

    private val buttons: ViewGroup by bindView(R.id.text_bottom_sheet_buttons)
    private val title: TextView by bindView(R.id.text_bottom_sheet_title)
    private val description: TextView by bindView(R.id.text_bottom_sheet_description)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receivedTexts.forEach { model ->
            createAndAddNewTextview(model)

            title.setTextOrGone(receivedTitle)
            description.setTextOrGone(receivedDescription)
        }
    }

    private fun createAndAddNewTextview(model: TextBottomSheetModel) {
        val textView = TextView(context)
        textView.layoutParams = LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }

        textView.setPadding(
              dp2px(if (model.icon.isNotEmpty()) 8 else 16).toInt(), //If it has icon remove the icon padding from left side
              dp2px(16).toInt(),
              dp2px(16).toInt(),
              dp2px(16).toInt()
        )

        textView.text = model.text
        textView.setTextAppearance(model.textStyle)
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.includeFontPadding = false

        if (model.icon.isNotEmpty()) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(model.icon, 0, 0, 0)
            textView.compoundDrawablePadding = dp2px(8).toInt()
        }

        textView.addRippleEffect()

        textView.setOnClickListener { model.clickCallback(delegate) }

        buttons.addView(textView)
    }

    companion object {
        private const val TAG = "TextBottomSheet"
        private const val ARGUMENT_TEXTS_KEY = "TextBottomSheet_Texts_Key"
        private const val ARGUMENT_TITLE_KEY = "TextBottomSheet_Title_Key"
        private const val ARGUMENT_DESCRIPTION_KEY = "TextBottomSheet_Description_Key"

        fun showTexts(
              supportFragmentManager: FragmentManager,
              texts: List<TextBottomSheetModel>,
              title: String? = null,
              description: String? = null
        ) {
            TextBottomSheet().apply {
                arguments = Bundle().apply {
                    putSerializable(ARGUMENT_TEXTS_KEY, texts.toTypedArray())
                    putString(ARGUMENT_TITLE_KEY, title)
                    putString(ARGUMENT_DESCRIPTION_KEY, description)
                }
            }.show(supportFragmentManager, TAG)
        }
    }
}