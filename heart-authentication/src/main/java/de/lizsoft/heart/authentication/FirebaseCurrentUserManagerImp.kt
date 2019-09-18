package de.lizsoft.heart.authentication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import de.lizsoft.heart.authentication.extension.convert
import de.lizsoft.heart.interfaces.authentication.FirebaseCurrentUserManager
import de.lizsoft.heart.interfaces.authentication.model.FirebaseUserModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber


class FirebaseCurrentUserManagerImp : FirebaseCurrentUserManager {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val updateSubject: BehaviorSubject<FirebaseUserModel> = BehaviorSubject.create()

    init {
        getCurrentUser()?.let { updateSubject.onNext(it) }
    }

    private fun getCurrentUser(): FirebaseUserModel? {
        return firebaseAuth.currentUser?.convert()
    }

    override fun firebaseUserUpdatePing() {
        firebaseAuth.currentUser?.convert()?.let {
            updateSubject.onNext(it)
        }
    }

    override fun changes(): Observable<FirebaseUserModel> = updateSubject
          .distinctUntilChanged()

    override fun updateCurrentUser(currentUserModel: FirebaseUserModel) {
        val profileUpdates = UserProfileChangeRequest.Builder()
              .setDisplayName(currentUserModel.displayName)
              .setPhotoUri(currentUserModel.avatarUrl)
              .build()

        FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
              ?.addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      Timber.d("User profile updated.")

                      firebaseUserUpdatePing()
                  }
              }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }

    override fun isAuthenticated(): Boolean {
        return getCurrentUser() != null
    }

    override fun isProfileComplete(): Boolean {
        val currentUser = getCurrentUser()
        return currentUser?.displayName.isNullOrBlank().not() &&
              currentUser?.displayName.isNullOrBlank().not()
    }
}