package de.lizsoft.app

import android.app.Application
import android.content.Context
import com.facebook.buck.android.support.exopackage.DefaultApplicationLike
import com.google.firebase.FirebaseApp
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import de.lizsoft.heart.Heart
import de.lizsoft.heart.activitylauncher.ActivityLauncher
import de.lizsoft.heart.authentication.HeartAuthentication
import de.lizsoft.heart.deeplink.HeartDeepLink
import de.lizsoft.heart.interfaces.koin.Qualifiers
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import de.lizsoft.heart.maptools.ui.HeartMap
import de.lizsoft.heart.pushnotification.HeartPushNotification
import de.lizsoft.travelcheck.dailycheck.TravelDailyCheckActivity
import de.lizsoft.travelcheck.di.travelCheckModule
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
import org.koin.core.module.Module
import org.koin.dsl.module
import timber.log.Timber

class App(private val application: Application) : DefaultApplicationLike(), KoinComponent {

    private val heartNavigator: HeartNavigator by lazy { get<HeartNavigator>() }

    private val applicationModule: Module = module {
        single<Context>(Qualifiers.applicationContext) { application.applicationContext }
        single(Qualifiers.applicationInstance) { application }
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        FirebaseApp.initializeApp(application.applicationContext)

        RxPaparazzo.register(application)

        Heart.bind(
              baseUrl = "https://reisen.lastminute.de/",
              modules = listOf(
                    applicationModule,
                    travelCheckModule
              )
        )
        Heart.addOkHttpInterceptors(get<AuthenticationInterceptor>())
        HeartMap.register(heartNavigator)
        HeartDeepLink.bind {
            heartNavigator.navigate(OpenTravelDailyCheckScreen)
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

        registerNavigations()
    }

    private fun registerNavigations() {
        heartNavigator.registerNavigation(
              OpenTravelCheckScreen::class.java
        ) { navigator, _ ->
            ActivityLauncher.with(navigator.getActivity()).open(TravelCheckActivity::class.java)
                  .startActivity()
        }
        heartNavigator.registerNavigation(
              OpenTravelDailyCheckScreen::class.java
        ) { navigator, _ ->
            ActivityLauncher.with(navigator.getActivity()).open(TravelDailyCheckActivity::class.java)
                  .startActivity()
        }
        heartNavigator.registerNavigation(
              OpenTravelOnBoardingScreen::class.java
        ) { navigator, _ ->
            ActivityLauncher.with(navigator.getActivity()).open(TravelOnBoardingActivity::class.java)
                  .startActivity()
        }

        heartNavigator.registerNavigation(
              OpenTravelSignInScreen::class.java
        ) { navigator, _ ->
            ActivityLauncher.with(navigator.getActivity())
                  .open(TravelSignInActivity::class.java)
                  .closeOtherActivities()
                  .startActivity()
        }
    }
}