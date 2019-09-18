package de.lizsoft.heart.common.implementation.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import de.lizsoft.heart.interfaces.common.ForegroundActivityService
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.scope.Scope
import timber.log.Timber

class LifeCycleObject(
    private val scope: Scope
) : KoinComponent {

    private val foregroundActivityService: ForegroundActivityService by inject()

    private var activityReadySubject: BehaviorSubject<ActivityWithPresenterInterface> = BehaviorSubject.create()
    protected val disposables = CompositeDisposable()

    private val lifecycleObserver: LifecycleObserver = object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            Timber.d("LifeCycleObject onDestroy called")
            unsubscribe()
            removeObserver()
        }
    }

    private lateinit var lifecycle: LifecycleOwner

    init {
        Timber.d("LifeCycleObject init scope:$scope")

        disposables += foregroundActivityService.getResumedScopedActivity(scope)
              .subscribe { activity ->
                  activityReadySubject.onNext(activity)
                  attackLifeCycle(activity as LifecycleOwner)
              }
    }

    private fun attackLifeCycle(lifecycleOwner: LifecycleOwner) {
        lifecycle = lifecycleOwner
        lifecycle.lifecycle.addObserver(lifecycleObserver)
    }

    private fun removeObserver() {
        lifecycle.lifecycle.removeObserver(lifecycleObserver)
    }

    private fun unsubscribe() {
        disposables.clear()
    }

    fun applyInActivity(callback: (ActivityWithPresenterInterface) -> Unit) {
        disposables += activityReadySubject.subscribe { activity ->
            callback(activity)
        }
    }

    fun getScopedActivity(): Maybe<ActivityWithPresenterInterface> = activityReadySubject
          .firstElement()
}