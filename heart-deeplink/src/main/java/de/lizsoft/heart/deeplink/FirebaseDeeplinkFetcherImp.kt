package de.lizsoft.heart.deeplink

import android.content.Intent
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import de.lizsoft.heart.interfaces.deeplink.FirebaseDeeplinkFetcher
import de.lizsoft.heart.interfaces.deeplink.model.Route
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import timber.log.Timber

internal class FirebaseDeeplinkFetcherImp(
      private val firebaseDynamicLinks: FirebaseDynamicLinks
) : FirebaseDeeplinkFetcher {

    override fun getRoute(intent: Intent): Single<Route> {
        val subject: SingleSubject<Route> = SingleSubject.create()

        firebaseDynamicLinks.getDynamicLink(intent)
              .addOnSuccessListener { pendingDynamicLinkData ->
                  Timber.d("Route received. path:${pendingDynamicLinkData?.link?.path}")
                  val splitedPath = pendingDynamicLinkData?.link?.path?.replaceFirst("/", "")?.split("/")
                  val route: String
                  val value: String
                  if (!splitedPath.isNullOrEmpty()) {
                      route = splitedPath[0]
                      value = if (splitedPath.size > 1) splitedPath[1] else ""
                  } else {
                      route = ""
                      value = ""
                  }

                  subject.onSuccess(Route(route, value))
              }
              .addOnFailureListener { e ->
                  Timber.e(e)
                  subject.onError(e)
              }

        return subject
    }
}