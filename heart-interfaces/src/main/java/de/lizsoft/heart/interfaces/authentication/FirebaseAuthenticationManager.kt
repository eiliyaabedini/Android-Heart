package de.lizsoft.heart.interfaces.authentication

import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import io.reactivex.Single
import org.koin.core.scope.Scope

interface FirebaseAuthenticationManager {

    fun initialise(currentScope: Scope)

    fun signIn(activity: ActivityWithPresenterInterface, handleShowingProgress: Boolean = false)
    fun signInObserve(activity: ActivityWithPresenterInterface): Single<Boolean>

    fun signOut(activity: ActivityWithPresenterInterface, handleShowingProgress: Boolean = false)
    fun signOutObserve(activity: ActivityWithPresenterInterface): Single<Boolean>
}