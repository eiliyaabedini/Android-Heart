package de.lizsoft.heart.deeplink

import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterView

class LinkDispatcherActivityPresenter : Presenter<LinkDispatcherActivityPresenter.View>() {
    override fun initialise() {

    }

    interface View : PresenterView

    sealed class Action : PresenterAction
}