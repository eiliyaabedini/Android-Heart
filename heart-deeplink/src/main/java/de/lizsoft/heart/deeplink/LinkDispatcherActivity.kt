package de.lizsoft.heart.deeplink

import android.app.NotificationManager
import android.content.Context
import de.lizsoft.heart.common.ui.ActivityWithPresenter
import de.lizsoft.heart.common.ui.ScreenBucket
import de.lizsoft.heart.interfaces.common.ForegroundActivityService
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import io.reactivex.rxkotlin.plusAssign
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.currentScope

class LinkDispatcherActivity : ActivityWithPresenter() {

    override fun getCurrentScreenBucket(): ScreenBucket<*> {
        return object : ScreenBucket<LinkDispatcherActivityPresenter.View>(
              ScreenBucketModel(
                    scope = currentScope
              )
        ) {
            override fun getPresenterView(): LinkDispatcherActivityPresenter.View {
                return object : LinkDispatcherActivityPresenter.View {

                }
            }
        }
    }

    private val deeplinkDispatcher: DeeplinkDispatcher by inject()
    private val foregroundActivityService: ForegroundActivityService by inject()

    override fun initializeViewListeners() {
        foregroundActivityService.currentResumed

        //Cancel all visible notifications when user opens the APP
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
              .cancelAll()

        disposables += deeplinkDispatcher.dispatch(intent)
              .subscribe {
                  finish()
              }
    }
}
