package de.lizsoft.heart.common.implementation.firebase.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.rx.doOnData
import de.lizsoft.heart.interfaces.common.rx.toSingleTaskResult
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class FirebaseRemoteConfigInitializerImp(
    private val remoteConfig: FirebaseRemoteConfig,
    private val reactiveTransformer: ReactiveTransformer
) : FirebaseRemoteConfigInitializer {

    private val updatedConfig: BehaviorSubject<Unit> = BehaviorSubject.create()

    init {
        remoteConfig.fetchAndActivate()
              .toSingleTaskResult()
              .subscribeOn(reactiveTransformer.ioScheduler())
              .doOnData { updatedConfig.onNext(Unit) }
              .subscribe()
    }

    override fun updateRemoteConfig(): Single<FirebaseRemoteConfig> {
        return updatedConfig
              .subscribeOn(reactiveTransformer.ioScheduler())
              .take(1)
              .firstOrError()
              .map { remoteConfig }
    }
}