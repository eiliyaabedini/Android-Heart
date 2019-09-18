package de.lizsoft.heart.common.ui.ui.dialogActivity

import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterView
import de.lizsoft.heart.interfaces.dialog.DialogActivityModel
import timber.log.Timber

class DialogActivityPresenter : Presenter<DialogActivityPresenter.View>() {

    override fun initialise() {
        updateContents()

        commonView?.actions()
              ?.subscribeSafeWithShowingErrorContent { action ->
                  Timber.d("action: $action")
                  when (action) {
                      is Action.CloseButtonClicked -> {
                          view?.closeDialog()
                      }

                      is Action.DialogNewIntentRecceived -> {
                          updateContents()
                      }
                  }
              }
    }

    private fun updateContents() {
        view?.getReceivedParams()?.let { view?.showContent(it) }
    }

    interface View : PresenterView {
        fun getReceivedParams(): DialogActivityModel?
        fun showContent(model: DialogActivityModel)
        fun closeDialog()
    }

    sealed class Action : PresenterAction {
        object CloseButtonClicked : Action()
        object DialogNewIntentRecceived : Action()
    }
}