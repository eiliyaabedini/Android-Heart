package de.lizsoft.heart.interfaces.common.firebase.messaging

import io.reactivex.Maybe

interface FirebaseMessagingDelegate {

    fun getToken(): Maybe<String>
}