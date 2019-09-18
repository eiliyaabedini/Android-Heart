package de.lizsoft.travelcheck.di

import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.koin.Qualifiers
import de.lizsoft.travelcheck.dailycheck.TravelDailyCheckActivity
import de.lizsoft.travelcheck.dailycheck.TravelDailyCheckActivityPresenter
import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.landing.TravelLandingActivity
import de.lizsoft.travelcheck.landing.TravelLandingActivityPresenter
import de.lizsoft.travelcheck.onboarding.TravelOnBoardingActivity
import de.lizsoft.travelcheck.onboarding.TravelOnBoardingActivityPresenter
import de.lizsoft.travelcheck.repository.LastMinutesTravelRepository
import de.lizsoft.travelcheck.repository.TravelMainRepository
import de.lizsoft.travelcheck.repository.TravelMainRepositoryImpl
import de.lizsoft.travelcheck.service.AuthenticationInterceptor
import de.lizsoft.travelcheck.service.TravelCheckLastMinutesApi
import de.lizsoft.travelcheck.signin.TravelSignInActivity
import de.lizsoft.travelcheck.signin.TravelSignInActivityPresenter
import de.lizsoft.travelcheck.travelcheck.TravelCheckActivity
import de.lizsoft.travelcheck.travelcheck.TravelCheckActivityPresenter
import de.lizsoft.travelcheck.usecase.TravelCheckFilterManager
import de.lizsoft.travelcheck.usecase.TravelCheckFilterManagerImp
import de.lizsoft.travelcheck.usecase.TravelCheckSortManager
import de.lizsoft.travelcheck.usecase.TravelCheckSortManagerImp
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val travelCheckModule = module {

    scope(named<TravelCheckActivity>()) {
        scoped<Presenter<TravelCheckActivityPresenter.View>> {
            TravelCheckActivityPresenter(get(), get(), get(), get())
        }
    }

    scope(named<TravelDailyCheckActivity>()) {
        scoped<Presenter<TravelDailyCheckActivityPresenter.View>> {
            TravelDailyCheckActivityPresenter(get(), get(), get(), get())
        }
    }

    scope(named<TravelSignInActivity>()) {
        scoped<Presenter<TravelSignInActivityPresenter.View>> {
            TravelSignInActivityPresenter(get(), get(), get(), get())
        }
    }

    scope(named<TravelLandingActivity>()) {
        scoped<Presenter<TravelLandingActivityPresenter.View>> {
            TravelLandingActivityPresenter(get(), get(), get(), get())
        }
    }

    scope(named<TravelOnBoardingActivity>()) {
        scoped<Presenter<TravelOnBoardingActivityPresenter.View>> {
            TravelOnBoardingActivityPresenter(get(), get(), get())
        }
    }

    single<TravelCheckLastMinutesApi> {
        get<Retrofit>(Qualifiers.noCachingApiRETROFIT).create(TravelCheckLastMinutesApi::class.java)
    }

    single {
        SettingsProvider(get())
    }

    single {
        LastMinutesTravelRepository(get(), get(), get())
    }

    factory<TravelCheckFilterManager> { TravelCheckFilterManagerImp(get()) }
    factory<TravelCheckSortManager> { TravelCheckSortManagerImp(get()) }

    factory { AuthenticationInterceptor() }

    single<TravelMainRepository> { TravelMainRepositoryImpl(get(), get(), get(), get(), get()) }
}
