package de.lizsoft.travelcheck.landing

import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.authentication.FirebaseCurrentUserManager
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterView
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.model.OpenTravelCheckScreen
import de.lizsoft.travelcheck.model.OpenTravelOnBoardingScreen
import de.lizsoft.travelcheck.model.OpenTravelSignInScreen
import timber.log.Timber

class TravelLandingActivityPresenter(
    private val heartNavigator: HeartNavigator,
    private val settingsProvider: SettingsProvider,
    private val firebaseCurrentUserManager: FirebaseCurrentUserManager,
    private val reactiveTransformer: ReactiveTransformer
) : Presenter<TravelLandingActivityPresenter.View>() {

    override fun initialise() {
        commonView?.actions()
              ?.subscribeOn(reactiveTransformer.ioScheduler())
              ?.subscribeSafeWithShowingErrorContent { action ->
                  Timber.d("action: $action")
                  when (action) {
                      is PresenterCommonAction.ErrorRetryClicked -> {
                          commonView?.showContent()
                      }
                  }
              }

        if (firebaseCurrentUserManager.isAuthenticated()) {
            if (isSettingsCompleted()) {
                heartNavigator.getLauncher(OpenTravelCheckScreen)?.startActivity()
            } else {
                heartNavigator.getLauncher(OpenTravelOnBoardingScreen)?.startActivity()
            }
        } else {
            heartNavigator.getLauncher(OpenTravelSignInScreen)?.startActivity()
        }
        commonView?.closeScreen()
    }

    private fun isSettingsCompleted(): Boolean {
        return true //TODO add checks here to see if we need to send user to onboarding after signup
    }

    interface View : PresenterView

    sealed class Action : PresenterAction
}
