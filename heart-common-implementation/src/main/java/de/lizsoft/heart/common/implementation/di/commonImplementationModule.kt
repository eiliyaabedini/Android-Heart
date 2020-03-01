package de.lizsoft.heart.common.implementation.di

import android.content.Context
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.experimental.builder.factoryBy
import org.koin.experimental.builder.singleBy
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


    singleBy<LocalStorageManager, LocalStorageManagerImp>()

    factory {
        PreferenceManager.getDefaultSharedPreferences(get(Qualifiers.applicationContext))
    }

    single {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
              .setDeveloperModeEnabled(BuildConfig.DEBUG)
              .setMinimumFetchIntervalInSeconds(3600)
              .build()
        remoteConfig.setConfigSettings(configSettings)
        //        remoteConfig.setDefaults(R.xml.remote_config_defaults)

        remoteConfig
    }

    singleBy<FirebaseRemoteConfigInitializer, FirebaseRemoteConfigInitializerImp>()

    singleBy<FirebaseRemoteConfigDelegate, FirebaseRemoteConfigDelegateImp>()

    single { FirebaseAnalytics.getInstance(get(Qualifiers.applicationContext)) }
    singleBy<FirebaseAnalyticsLogger, FirebaseAnalyticsLoggerImp>()
    singleBy<EventTracker, EventTrackerImp>()

    factoryBy<PermissionHandler, PermissionHandlerImp>()

    single<OkHttpClient>(Qualifiers.noCachingApiOKHTTP) {
        val client = OkHttpClient.Builder()
              .connectTimeout(1, TimeUnit.MINUTES)
              .readTimeout(1, TimeUnit.MINUTES)
              .writeTimeout(1, TimeUnit.MINUTES)

        client.build()
    }

    single<OkHttpClient>(Qualifiers.cachingApiOKHTTP) {
        OkHttpClient.Builder()
              .connectTimeout(1, TimeUnit.MINUTES)
              .readTimeout(1, TimeUnit.MINUTES)
              .writeTimeout(1, TimeUnit.MINUTES)
              .cache(
                    Cache(
                          get<Context>(Qualifiers.applicationContext).cacheDir,
                          (5 * 1024 * 1024).toLong()
                    )
              ).build()
    }
}

fun heartCommonImplementationModuleWithParams(
      baseUrl: String
): Module = module {

    single(Qualifiers.baseApiUrl) { baseUrl }

    single<Retrofit>(Qualifiers.noCachingApiRETROFIT) {
        Retrofit.Builder()
              .baseUrl(baseUrl)
              .client(get<OkHttpClient>(Qualifiers.noCachingApiOKHTTP))
              .addConverterFactory(GsonConverterFactory.create())
              .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .build()
    }

    single<Retrofit>(Qualifiers.cachingApiRETROFIT) {
        Retrofit.Builder()
              .baseUrl(baseUrl)
              .client(get<OkHttpClient>(Qualifiers.cachingApiOKHTTP))
              .addConverterFactory(GsonConverterFactory.create())
              .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .build()
    }
}
