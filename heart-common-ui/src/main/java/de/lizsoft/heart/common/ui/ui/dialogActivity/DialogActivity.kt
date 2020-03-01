package de.lizsoft.heart.common.ui.ui.dialogActivity

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.lizsoft.heart.interfaces.navigator.retrieveArgument
import de.lizsoft.heart.common.ui.ActivityWithPresenter
import de.lizsoft.heart.common.ui.R
import de.lizsoft.heart.common.ui.ScreenBucket
import de.lizsoft.heart.common.ui.extension.bindView
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import de.lizsoft.heart.interfaces.dialog.DialogActivityModel
import org.koin.androidx.scope.currentScope
import timber.log.Timber

class DialogActivity : ActivityWithPresenter() {

    override fun getCurrentScreenBucket(): ScreenBucket<*> = object : ScreenBucket<DialogActivityPresenter.View>(
          ScreenBucketModel(
                scope = currentScope,
                viewLayout = R.layout.activity_dialog
          )
    ) {
        override fun getPresenterView(): DialogActivityPresenter.View {
            return object : DialogActivityPresenter.View {

                override fun getReceivedParams(): DialogActivityModel? = dialogActivityModelArgument

                override fun showContent(model: DialogActivityModel) {
                    runOnUiThread {
                        imgIcon.setImageResource(model.imageDrawable)
                        txtTitle.text = model.title
                        txtDescription.text = model.description
                        txtSecondDescription.text = model.secondDescription
                    }
                }

                override fun closeDialog() {
                    runOnUiThread {
                        finish()
                    }
                }
            }
        }
    }

    private val imgIcon: ImageView by bindView(R.id.dialog_icon_image)
    private val txtTitle: TextView by bindView(R.id.dialog_title_text)
    private val txtDescription: TextView by bindView(R.id.dialog_description_text)
    private val txtSecondDescription: TextView by bindView(R.id.dialog_second_description_text)
    private val viewDivider: View by bindView(R.id.dialog_divider_view)
    private val btnClose: View by bindView(R.id.dialog_close_icon)

    private var dialogActivityModelArgument: DialogActivityModel? = null

    override fun initializeBeforePresenter() {
        dialogActivityModelArgument = retrieveArgument()
        Timber.d("dialogActivityModelArgument:${dialogActivityModelArgument.toString()}")
    }

    override fun initializeViewListeners() {
        btnClose.setOnClickListener {
            actions.onNext(DialogActivityPresenter.Action.CloseButtonClicked)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        //dialog requested to open while it was already open. lets refresh the received params here and notify presenter for changes
        dialogActivityModelArgument = retrieveArgument(intent)

        actions.onNext(DialogActivityPresenter.Action.DialogNewIntentRecceived)
    }
}
