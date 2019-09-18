package de.lizsoft.heart.interfaces.common

import android.app.Application
import io.reactivex.Maybe
import io.reactivex.Observable
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import org.koin.core.scope.Scope

interface ForegroundActivityService : Application.ActivityLifecycleCallbacks {
    val currentResumed: Optional<ActivityWithPresenterInterface>
    val resumed: Observable<Optional<ActivityWithPresenterInterface>>

    fun getResumedScopedActivity(scope: Scope): Maybe<ActivityWithPresenterInterface>

    fun isApplicationVisible(): Boolean
}