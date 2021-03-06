package de.lizsoft.app

import android.app.Application
import com.facebook.buck.android.support.exopackage.DefaultApplicationLike
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import de.lizsoft.heart.Heart
import de.lizsoft.heart.authentication.HeartAuthentication
import de.lizsoft.heart.common.event.DataEvent
import de.lizsoft.heart.common.event.EventManager
import de.lizsoft.heart.deeplink.HeartDeepLink
import de.lizsoft.heart.errorhandler.HeartErrorHandler
import de.lizsoft.heart.interfaces.navigator.ActivityLauncher
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import de.lizsoft.heart.pushnotification.HeartPushNotification
import de.lizsoft.travelcheck.dailycheck.TravelDailyCheckActivity
import de.lizsoft.travelcheck.di.travelCheckModule
import de.lizsoft.travelcheck.error.BackendException
import de.lizsoft.travelcheck.model.OpenTravelCheckScreen
import de.lizsoft.travelcheck.model.OpenTravelDailyCheckScreen
import de.lizsoft.travelcheck.model.OpenTravelOnBoardingScreen
import de.lizsoft.travelcheck.model.OpenTravelSignInScreen
import de.lizsoft.travelcheck.onboarding.TravelOnBoardingActivity
import de.lizsoft.travelcheck.service.AuthenticationInterceptor
import de.lizsoft.travelcheck.signin.TravelSignInActivity
import de.lizsoft.travelcheck.travelcheck.TravelCheckActivity
import org.koin.core.KoinComponent
import org.koin.core.get
import timber.log.Timber

class App(private val application: Application) : DefaultApplicationLike(), KoinComponent {

    private val heartNavigator: HeartNavigator by lazy { get<HeartNavigator>() }
    private val eventManager: EventManager by lazy { get<EventManager>() }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        RxPaparazzo.register(application)

        Heart.bind(
              application = application,
              baseUrl = "https://reisen.lastminute.de/",
              modules = listOf(
                    travelCheckModule
              )
        )
        Heart.addOkHttpInterceptors(listOf(get<AuthenticationInterceptor>()))
        //        HeartMap.bind()
        //        HeartMapUI.bind(heartNavigator)
        HeartDeepLink.bind {
            heartNavigator.getLauncher(OpenTravelDailyCheckScreen)?.startActivity()
        }
        HeartPushNotification.bind(
              listOfKeys = emptyList(),
              shouldReceivePushNotification = { true },
              tokenRenewed = {}
        )
        HeartAuthentication.bind(
              signInScreenObject = OpenTravelSignInScreen,
              signedInScreenObject = OpenTravelCheckScreen
        )

        HeartErrorHandler.bind(
              isTesting = BuildConfig.DEBUG,
              dispatcher = { throwable ->
                  if (throwable is BackendException) {
                      if (throwable.isAuthenticationError()) {
                          eventManager.notify(DataEvent.NotAuthenticated)
                      }
                  }
              }
        )

        registerNavigations()
    }

    private fun registerNavigations() {
        heartNavigator.registerNavigation(
              OpenTravelCheckScreen::class.java
        ) { navigator, _ ->
            ActivityLauncher.with(navigator.getActivity()).open(TravelCheckActivity::class.java)
        }
        heartNavigator.registerNavigation(
              OpenTravelDailyCheckScreen::class.java
        ) { navigator, _ ->
            ActivityLauncher.with(navigator.getActivity()).open(TravelDailyCheckActivity::class.java)
        }
        heartNavigator.registerNavigation(
              OpenTravelOnBoardingScreen::class.java
        ) { navigator, _ ->
            ActivityLauncher.with(navigator.getActivity()).open(TravelOnBoardingActivity::class.java)
        }

        heartNavigator.registerNavigation(
              OpenTravelSignInScreen::class.java
        ) { navigator, _ ->
            ActivityLauncher.with(navigator.getActivity())
                  .open(TravelSignInActivity::class.java)
                  .closeOtherActivities()
        }
    }
}
