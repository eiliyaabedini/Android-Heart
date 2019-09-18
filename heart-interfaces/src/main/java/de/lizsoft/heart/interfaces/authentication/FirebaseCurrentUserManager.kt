package de.lizsoft.heart.interfaces.authentication

import de.lizsoft.heart.interfaces.authentication.model.FirebaseUserModel
import io.reactivex.Observable

interface FirebaseCurrentUserManager {

    fun changes(): Observable<FirebaseUserModel>
    fun firebaseUserUpdatePing()
    fun updateCurrentUser(currentUserModel: FirebaseUserModel)

    fun isEmailVerified(): Boolean
    fun isAuthenticated(): Boolean
    fun isProfileComplete(): Boolean

    fun signOut()
}