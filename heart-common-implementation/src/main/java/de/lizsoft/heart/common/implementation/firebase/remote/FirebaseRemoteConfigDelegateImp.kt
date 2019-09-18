package de.lizsoft.heart.common.implementation.firebase.remote

import de.lizsoft.heart.interfaces.common.firebase.remote.FirebaseRemoteConfigDelegate
import io.reactivex.Single

class FirebaseRemoteConfigDelegateImp(
    private val remoteConfigInitializer: FirebaseRemoteConfigInitializer
) : FirebaseRemoteConfigDelegate {

    override fun getTestString(): Single<String> {
        return getStringWithDefaultValue("TestString", "")
    }

    override fun getTestBoolean(): Single<Boolean> {
        return getBooleanWithDefaultValue("TestBoolean")
    }

    private fun getStringWithDefaultValue(key: String, default: String): Single<String> {
        return remoteConfigInitializer.updateRemoteConfig()
              .map { config ->
                  val string = config.getString(key)
                  return@map if (string.isNullOrBlank()) {
                      default
                  } else {
                      string
                  }
              }
    }

    private fun getBooleanWithDefaultValue(key: String): Single<Boolean> {
        return remoteConfigInitializer.updateRemoteConfig()
              .map { config ->
                  config.getBoolean(key)
              }
    }
}