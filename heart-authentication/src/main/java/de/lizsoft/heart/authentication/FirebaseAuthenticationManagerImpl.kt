package de.lizsoft.heart.authentication

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import de.lizsoft.heart.interfaces.ResponseResult
import de.lizsoft.heart.interfaces.authentication.FirebaseAuthenticationManager
import de.lizsoft.heart.interfaces.authentication.FirebaseCurrentUserManager
import de.lizsoft.heart.interfaces.common.ForegroundActivityService
import de.lizsoft.heart.interfaces.common.rx.toMaybeResult
import de.lizsoft.heart.interfaces.common.ui.ActivityResult
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.koin.core.scope.Scope
import timber.log.Timber

class FirebaseAuthenticationManagerImpl(
      private val heartNavigator: HeartNavigator,
      private val firebaseCurrentUserManager: FirebaseCurrentUserManager,
      private val foregroundActivityService: ForegroundActivityService
) : FirebaseAuthenticationManager {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val disposables = CompositeDisposable()

    override fun initialise(currentScope: Scope) {
        Timber.w("initialise")

        disposables.clear()

        disposables += foregroundActivityService.getResumedScopedActivity(currentScope)
              .subscribe { activity ->
                  val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(BuildConfig.CONFIG_FIREBASE_GOOGLE_SIGN_IN_WEB_CLIENT_ID)
                        .requestEmail()
                        .build()

                  googleSignInClient = GoogleSignIn.getClient(activity as Activity, gso)

                  auth = FirebaseAuth.getInstance()
              }
    }

    override fun signIn(activity: ActivityWithPresenterInterface, handleShowingProgress: Boolean) {
        if (handleShowingProgress) activity.getCommonView().showContentLoading()

        disposables += signInObserve(activity)
              .filter { it }
              .doOnSuccess {
                  heartNavigator.navigate(
                        HeartAuthentication.signedInScreenObject
                  )
                  activity.getCommonView().closeScreen()
              }
              .doOnError {
                  if (handleShowingProgress) activity.getCommonView().showContent()
              }
              .subscribe()
    }

    override fun signInObserve(activity: ActivityWithPresenterInterface): Single<Boolean> {
        Timber.w("signinObserve")
        activity.startActivityForResultFromParent(googleSignInClient.signInIntent, RC_SIGN_IN)

        return activity.observeActivityResult()
              .filter { it.requestCode == RC_SIGN_IN }
              .map {
                  onActivityResult(
                        activity = activity,
                        activityResult = it
                  )
              }
              .first(false)
    }

    private fun onActivityResult(activity: ActivityWithPresenterInterface, activityResult: ActivityResult): Boolean {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(activity, account!!)
            return true
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Timber.w("Google sign in failed")
            e.printStackTrace()
        }
        return false
    }

    private fun firebaseAuthWithGoogle(activity: ActivityWithPresenterInterface, acct: GoogleSignInAccount) {
        Timber.d("firebaseAuthWithGoogle:${acct.id}")
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
              .addOnCompleteListener(activity as Activity) { task ->
                  if (task.isSuccessful) {
                      Timber.d("signInWithCredential:success")
                      firebaseCurrentUserManager.firebaseUserUpdatePing()
                  } else {
                      Timber.w("signInWithCredential:failure:${task.exception.toString()}")
                  }
              }
    }

    override fun signOutObserve(activity: ActivityWithPresenterInterface): Single<Boolean> {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        return googleSignInClient.signOut()
              .toMaybeResult()
              .map { response ->
                  when (response) {
                      is ResponseResult.Success -> true
                      is ResponseResult.Failure -> false
                  }
              }
              .toSingle(false)
    }

    override fun signOut(activity: ActivityWithPresenterInterface, handleShowingProgress: Boolean) {
        if (handleShowingProgress) activity.getCommonView().showContentLoading()

        disposables += signOutObserve(activity)
              .filter { it }
              .doOnSuccess {
                  heartNavigator.navigate(
                        HeartAuthentication.signInScreenObject
                  )
              }
              .doOnError {
                  if (handleShowingProgress) activity.getCommonView().showContent()
              }
              .subscribe()
    }

    companion object {
        private const val RC_SIGN_IN = 1123
    }
}