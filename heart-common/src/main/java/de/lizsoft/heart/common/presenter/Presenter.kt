package de.lizsoft.heart.common.presenter

import androidx.annotation.CallSuper
import de.lizsoft.heart.interfaces.ResponseResult
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonView
import de.lizsoft.heart.interfaces.common.presenter.PresenterView
import de.lizsoft.heart.interfaces.common.rx.doOnFailure
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

abstract class Presenter<V : PresenterView> {

    protected val disposables = CompositeDisposable()
    protected val disposablesVisibleView = CompositeDisposable()

    private var _view: V? = null
    private var _commonView: PresenterCommonView? = null

    protected val view: V?
        get() = _view

    protected val commonView: PresenterCommonView?
        get() = _commonView

    fun attachView(view: V, commonView: PresenterCommonView) {
        this._view = view
        this._commonView = commonView

        Timber.i("${javaClass.simpleName} attachView called.")
    }

    fun detachView() {
        Timber.i("${javaClass.simpleName} detachView called.")

        disposables.clear()
        disposablesVisibleView.clear()

        _view = null
        _commonView = null
    }

    fun isViewAttached(): Boolean = _view != null && _commonView != null

    abstract fun initialise()

    @CallSuper
    open fun viewIsVisible() {
    }

    @CallSuper
    open fun viewIsHidden() {
        disposablesVisibleView.clear()
    }

    protected fun <T> Observable<T>.subscribeSafeWithShowingErrorContent(onNext: ((T) -> Unit)? = null): Disposable {
        val disposable = subscribe(
              if (onNext == null) {
                  Functions.emptyConsumer<T>()
              } else {
                  Consumer<T> { response -> onNext(response) }
              },
              Consumer<Throwable> {
                  it.printStackTrace()
                  commonView?.showContentError()
              },
              Functions.EMPTY_ACTION,
              Functions.emptyConsumer<Disposable>()
        )

        disposables += disposable
        return disposable
    }

    protected fun <T> Observable<ResponseResult<T>>.subscribeSafeResponseWithShowingErrorContent(): Disposable {
        val disposable = doOnFailure {
            it.printStackTrace()
            commonView?.showContentError()
        }
              .subscribe(
                    Functions.emptyConsumer<ResponseResult<T>>(),
                    Consumer<Throwable> {
                        it.printStackTrace()
                        commonView?.showContentError()
                    },
                    Functions.EMPTY_ACTION,
                    Functions.emptyConsumer<Disposable>()
              )

        disposables += disposable
        return disposable
    }

    protected fun <T> Single<T>.subscribeSafeWithShowingErrorContent(): Disposable {
        val disposable = subscribe(
              Functions.emptyConsumer<T>(),
              Consumer<Throwable> {
                  it.printStackTrace()
                  commonView?.showContentError()
              }
        )

        disposables += disposable
        return disposable
    }

    protected fun <T> Single<ResponseResult<T>>.subscribeSafeResponseWithShowingErrorContent(): Disposable {
        val disposable = doOnFailure {
            it.printStackTrace()
            commonView?.showContentError()
        }
              .subscribe(
                    Functions.emptyConsumer<ResponseResult<T>>(),
                    Consumer<Throwable> {
                        it.printStackTrace()
                        commonView?.showContentError()
                    }
              )

        disposables += disposable
        return disposable
    }

    protected fun <T> Maybe<T>.subscribeSafeWithShowingErrorContent(): Disposable {
        val disposable = subscribe(
              Functions.emptyConsumer<T>(),
              Consumer<Throwable> {
                  it.printStackTrace()
                  commonView?.showContentError()
              },
              Functions.EMPTY_ACTION
        )

        disposables += disposable
        return disposable
    }

    protected fun <T> Maybe<ResponseResult<T>>.subscribeSafeResponseWithShowingErrorContent(): Disposable {
        val disposable = doOnFailure {
            it.printStackTrace()
            commonView?.showContentError()
        }
              .subscribe(
                    Functions.emptyConsumer<ResponseResult<T>>(),
                    Consumer<Throwable> {
                        it.printStackTrace()
                        commonView?.showContentError()
                    },
                    Functions.EMPTY_ACTION
              )

        disposables += disposable
        return disposable
    }

    protected fun Completable.subscribeSafeWithShowingErrorContent(): Disposable {
        val disposable = subscribe(
              Functions.EMPTY_ACTION,
              Consumer<Throwable> {
                  it.printStackTrace()
                  commonView?.showContentError()
              }
        )

        disposables += disposable
        return disposable
    }
}
