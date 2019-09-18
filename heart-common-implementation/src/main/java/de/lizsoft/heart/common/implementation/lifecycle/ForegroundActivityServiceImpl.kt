package de.lizsoft.heart.common.implementation.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import de.lizsoft.heart.interfaces.common.*
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.koin.core.scope.Scope
import timber.log.Timber

internal class ForegroundActivityServiceImpl(
    application: Application
) : ForegroundActivityService {

    private val resumedSubject = BehaviorSubject.create<Optional<ActivityWithPresenterInterface>>()

    init {
        Timber.d("ForegroundActivityServiceImpl init called")
        application.registerActivityLifecycleCallbacks(this)
    }

    override val currentResumed: Optional<ActivityWithPresenterInterface>
        get() {
            val latestValue = resumedSubject.value
            return if (latestValue != null) {
                latestValue
            } else {
                Optional.None
            }
        }

    override val resumed: Observable<Optional<ActivityWithPresenterInterface>>
        get() = resumedSubject

    override fun isApplicationVisible(): Boolean {
        return currentResumed.isSome
    }

    override fun getResumedScopedActivity(scope: Scope): Maybe<ActivityWithPresenterInterface> {
        return resumedSubject
              .filter { it.isSome }
              .map { it.get() }
              .ofType(ActivityWithPresenterInterface::class.java)
              .filter { activity -> activity.getCurrentScreenBucketModel().scope == scope }
              .firstElement()
              .doOnSuccess { Timber.d("getResumedScopedActivity activity found") }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        if (activity is ActivityWithPresenterInterface) {
            resumedSubject.onNext((activity as ActivityWithPresenterInterface).toOptional())
        }
    }

    override fun onActivityPaused(activity: Activity) {
        resumedSubject.onNext(Optional.None)
    }

    @VisibleForTesting
    fun resumeForTest(activity: ActivityWithPresenterInterface) {
        resumedSubject.onNext(activity.toOptional())
    }

    @VisibleForTesting
    fun pauseForTest() {
        resumedSubject.onNext(Optional.None)
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}

    override fun onActivityDestroyed(activity: Activity) {}
}