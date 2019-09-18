package de.lizsoft.travelcheck.landing

import de.lizsoft.heart.common.ui.ActivityWithPresenter
import de.lizsoft.heart.common.ui.ScreenBucket
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import org.koin.androidx.scope.currentScope

class TravelLandingActivity : ActivityWithPresenter() {

    override fun getCurrentScreenBucket(): ScreenBucket<*> {
        return object : ScreenBucket<TravelLandingActivityPresenter.View>(
              ScreenBucketModel(
                    scope = currentScope,
                    enableDisplayHomeAsUp = false
              )
        ) {
            override fun getPresenterView(): TravelLandingActivityPresenter.View {
                return object : TravelLandingActivityPresenter.View {

                }
            }
        }
    }
}