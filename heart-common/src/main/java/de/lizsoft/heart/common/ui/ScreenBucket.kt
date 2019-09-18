package de.lizsoft.heart.common.ui

import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonView
import de.lizsoft.heart.interfaces.common.presenter.PresenterView
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import org.koin.core.KoinComponent

abstract class ScreenBucket<V : PresenterView>(
    val screenBucketModel: ScreenBucketModel
) : KoinComponent {

    private val presenter: Presenter<V> by screenBucketModel.scope.inject()

    fun attachView(commonView: PresenterCommonView) {
        presenter.attachView(getPresenterView(), commonView)
    }

    fun initialise() {
        presenter.initialise()
    }

    fun detachView() {
        presenter.detachView()
    }

    fun viewIsVisible() {
        presenter.viewIsVisible()
    }

    fun viewIsHidden() {
        presenter.viewIsHidden()
    }

    abstract fun getPresenterView(): V
}