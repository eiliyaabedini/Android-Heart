package de.lizsoft.heart.common.implementation.di

import android.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import de.lizsoft.heart.common.implementation.BuildConfig
import de.lizsoft.heart.common.implementation.ReactiveTransformerImp
import de.lizsoft.heart.common.implementation.event.EventTrackerImp
import de.lizsoft.heart.common.implementation.event.FirebaseAnalyticsLoggerImp
import de.lizsoft.heart.common.implementation.firebase.messaging.FirebaseMessagingDelegateImp
import de.lizsoft.heart.common.implementation.firebase.remote.FirebaseRemoteConfigDelegateImp
import de.lizsoft.heart.common.implementation.firebase.remote.FirebaseRemoteConfigInitializer
import de.lizsoft.heart.common.implementation.firebase.remote.FirebaseRemoteConfigInitializerImp
import de.lizsoft.heart.common.implementation.lifecycle.ForegroundActivityServiceImpl
import de.lizsoft.heart.common.implementation.service.AddressServiceImp
import de.lizsoft.heart.common.implementation.service.PermissionHandlerImp
import de.lizsoft.heart.common.implementation.storage.LocalStorageManagerImp
import de.lizsoft.heart.interfaces.common.ForegroundActivityService
import de.lizsoft.heart.interfaces.common.LocalStorageManager
import de.lizsoft.heart.interfaces.common.PermissionHandler
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.event.EventTracker
import de.lizsoft.heart.interfaces.common.event.FirebaseAnalyticsLogger
import de.lizsoft.heart.interfaces.common.firebase.messaging.FirebaseMessagingDelegate
import de.lizsoft.heart.interfaces.common.firebase.remote.FirebaseRemoteConfigDelegate
import de.lizsoft.heart.interfaces.koin.Qualifiers
import de.lizsoft.heart.interfaces.map.service.AddressService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val heartCommonImplementationModule: Module = module {

    single<ForegroundActivityService> {
        ForegroundActivityServiceImpl(
              get(
                    Qualifiers.applicationInstance
              )
        )
    }

    factory<FirebaseMessagingDelegate> { FirebaseMessagingDelegateImp() }

    factory<ReactiveTransformer> {
        ReactiveTransformerImp(
              io = Schedulers.io(),
              trampoline = Schedulers.trampoline(),
              computation = Schedulers.computation(),
              single = Schedulers.single(),
              mainThread = AndroidSchedulers.mainThread()
        )
    }


    single<LocalStorageManager> { LocalStorageManagerImp(get(), get()) }

    factory {
        PreferenceManager.getDefaultSharedPreferences(get(Qualifiers.applicationContext))
    }

    single<FirebaseRemoteConfig> {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
              .setDeveloperModeEnabled(BuildConfig.DEBUG)
              .setMinimumFetchIntervalInSeconds(3600)
              .build()
        remoteConfig.setConfigSettings(configSettings)
        //        remoteConfig.setDefaults(R.xml.remote_config_defaults)

        remoteConfig
    }

    single<FirebaseRemoteConfigInitializer> {
        FirebaseRemoteConfigInitializerImp(
              get(),
              get()
        )
    }
    single<FirebaseRemoteConfigDelegate> {
        FirebaseRemoteConfigDelegateImp(
              get()
        )
    }

    single { FirebaseAnalytics.getInstance(get(Qualifiers.applicationContext)) }
    single<FirebaseAnalyticsLogger> { FirebaseAnalyticsLoggerImp(get()) }
    single<EventTracker> { EventTrackerImp(get()) }

    factory<PermissionHandler> { PermissionHandlerImp(get()) }

    factory<AddressService> { AddressServiceImp(get(), get()) }

    factory<OkHttpClient>(Qualifiers.noCachingApiOKHTTP) {
        val client = OkHttpClient.Builder()
              .connectTimeout(1, TimeUnit.MINUTES)
              .readTimeout(1, TimeUnit.MINUTES)
              .writeTimeout(1, TimeUnit.MINUTES)

        client.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        client.build()
    }
}

fun heartCommonImplementationModuleWithParams(
      baseUrl: String?
): Module = module {

    if (baseUrl != null) {
        single<Retrofit>(Qualifiers.noCachingApiRETROFIT) {
            Retrofit.Builder()
                  .baseUrl(baseUrl)
                  .client(get<OkHttpClient>(Qualifiers.noCachingApiOKHTTP))
                  .addConverterFactory(GsonConverterFactory.create())
                  .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                  .build()
        }
    }
}