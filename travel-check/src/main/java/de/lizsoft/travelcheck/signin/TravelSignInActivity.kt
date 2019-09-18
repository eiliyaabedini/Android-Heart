package de.lizsoft.travelcheck.signin

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import de.lizsoft.heart.common.ui.ActivityWithPresenter
import de.lizsoft.heart.common.ui.ScreenBucket
import de.lizsoft.heart.common.ui.extension.bindView
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import de.lizsoft.travelcheck.R
import org.koin.androidx.scope.currentScope

class TravelSignInActivity : ActivityWithPresenter() {

    override fun getCurrentScreenBucket(): ScreenBucket<*> {
        return object : ScreenBucket<TravelSignInActivityPresenter.View>(
              ScreenBucketModel(
                    scope = currentScope,
                    viewLayout = R.layout.activity_travel_signin,
                    hideSystemBar = false
              )
        ) {
            override fun getPresenterView(): TravelSignInActivityPresenter.View {
                return object : TravelSignInActivityPresenter.View {

                }
            }
        }
    }

    private val btnGoogleSignIn: View by bindView(R.id.travel_check_signin_google)
    private val bgView: ImageView by bindView(R.id.travel_check_signin_backgroung)

    override fun initializeBeforePresenter() {
        Glide.with(this)
              .load("https://images.pexels.com/photos/1020016/pexels-photo-1020016.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500")
              .centerCrop()
              .into(bgView)
    }

    override fun initializeViewListeners() {
        btnGoogleSignIn.setOnClickListener {
            actions.onNext(TravelSignInActivityPresenter.Action.GoogleSignInClicked)
        }
    }
}