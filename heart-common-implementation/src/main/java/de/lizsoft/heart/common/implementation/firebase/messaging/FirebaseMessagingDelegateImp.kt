package de.lizsoft.heart.common.implementation.firebase.messaging

import com.google.firebase.iid.FirebaseInstanceId
import de.lizsoft.heart.interfaces.common.firebase.messaging.FirebaseMessagingDelegate
import io.reactivex.Maybe
import io.reactivex.subjects.MaybeSubject
import timber.log.Timber

class FirebaseMessagingDelegateImp : FirebaseMessagingDelegate {

    override fun getToken(): Maybe<String> {
        Timber.d("firebase messaging token:${FirebaseInstanceId.getInstance().token}")

        val subject: MaybeSubject<String> = MaybeSubject.create()

        FirebaseInstanceId.getInstance().instanceId
              .addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      // Get new Instance ID token
                      task.result?.let {
                          subject.onSuccess(it.token)
                          Timber.d("Firebase token:${it.token}")
                      }
                  } else {
                      Timber.e(task.exception)
                  }
              }

        return subject
    }

}