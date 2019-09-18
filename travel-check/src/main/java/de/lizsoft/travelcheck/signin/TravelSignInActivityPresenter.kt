package de.lizsoft.travelcheck.signin

import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.authentication.FirebaseAuthenticationManager
import de.lizsoft.heart.interfaces.authentication.FirebaseCurrentUserManager
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterView
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import de.lizsoft.travelcheck.model.OpenTravelCheckScreen
import de.lizsoft.travelcheck.model.OpenTravelOnBoardingScreen
import timber.log.Timber

class TravelSignInActivityPresenter(
      private val heartNavigator: HeartNavigator,
      private val firebaseCurrentUserManager: FirebaseCurrentUserManager,
      private val firebaseAuthenticationManager: FirebaseAuthenticationManager,
      private val reactiveTransformer: ReactiveTransformer
) : Presenter<TravelSignInActivityPresenter.View>() {

    override fun initialise() {
        firebaseAuthenticationManager.initialise(commonView!!.getCurrentScope())

        commonView?.actions()
              ?.subscribeOn(reactiveTransformer.ioScheduler())
              ?.subscribeSafeWithShowingErrorContent { action ->
                  Timber.d("action: $action")
                  when (action) {
                      is PresenterCommonAction.ErrorRetryClicked -> {
                          commonView?.showContent()
                      }

                      is Action.GoogleSignInClicked -> {
                          commonView?.getActivity()?.let {
                              firebaseAuthenticationManager.signIn(it, true)
                          }
                      }
                  }
              }

        if (firebaseCurrentUserManager.isAuthenticated()) {
            if (firebaseCurrentUserManager.isProfileComplete()) {
                heartNavigator.navigate(OpenTravelCheckScreen)
                commonView?.closeScreen()
            } else {
                heartNavigator.navigate(OpenTravelOnBoardingScreen)
                commonView?.closeScreen()
            }
        }
    }


    interface View : PresenterView

    sealed class Action : PresenterAction {
        object GoogleSignInClicked : Action()
    }
}