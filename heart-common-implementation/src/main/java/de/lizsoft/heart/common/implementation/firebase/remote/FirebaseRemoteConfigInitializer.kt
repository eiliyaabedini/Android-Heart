package de.lizsoft.heart.common.implementation.firebase.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.reactivex.Single

interface FirebaseRemoteConfigInitializer {

    fun updateRemoteConfig(): Single<FirebaseRemoteConfig>
}