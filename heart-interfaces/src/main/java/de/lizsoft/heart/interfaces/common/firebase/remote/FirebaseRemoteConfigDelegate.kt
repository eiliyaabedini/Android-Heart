package de.lizsoft.heart.interfaces.common.firebase.remote

import io.reactivex.Single

interface FirebaseRemoteConfigDelegate {

    fun getTestString(): Single<String>

    fun getTestBoolean(): Single<Boolean>
}