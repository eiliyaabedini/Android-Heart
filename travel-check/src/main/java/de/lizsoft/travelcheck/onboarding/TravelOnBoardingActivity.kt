package de.lizsoft.travelcheck.onboarding

import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import de.lizsoft.heart.common.ui.ActivityWithPresenter
import de.lizsoft.heart.common.ui.ScreenBucket
import de.lizsoft.heart.common.ui.extension.bindView
import de.lizsoft.heart.interfaces.authentication.FirebaseAuthenticationManager
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import de.lizsoft.travelcheck.R
import de.lizsoft.travelcheck.onboarding.model.OnBoardingModel
import de.lizsoft.travelcheck.onboarding.renderer.TravelCheckOnBoardingRenderer
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.currentScope

class TravelOnBoardingActivity : ActivityWithPresenter() {

    override fun getCurrentScreenBucket(): ScreenBucket<*> {
        return object : ScreenBucket<TravelOnBoardingActivityPresenter.View>(
              ScreenBucketModel(
                    scope = currentScope,
                    viewLayout = R.layout.activity_travel_onboarding,
                    enableDisplayHomeAsUp = true
              )
        ) {
            override fun getPresenterView(): TravelOnBoardingActivityPresenter.View {
                return object : TravelOnBoardingActivityPresenter.View {
                    override fun showItems(items: List<ViewModel>) {
                        runOnUiThread {
                            adapter.setItems(items)
                        }
                    }
                }
            }
        }
    }

    private val firebaseAuthenticationManager: FirebaseAuthenticationManager by inject()

    private val listContent: RecyclerView by bindView(R.id.travel_check_onboarding_content_list)

    private val adapter = RendererRecyclerViewAdapter()

    override fun initializeViewListeners() {

        firebaseAuthenticationManager.initialise(currentScope = currentScope)
        findViewById<View>(R.id.travel_check_onboarding_sign_out).setOnClickListener {
            firebaseAuthenticationManager.signOut(this, true)
        }

        adapter.registerRenderer(
              ViewBinder(
                    R.layout.travel_check_onboarding_renderer,
                    OnBoardingModel::class.java,
                    TravelCheckOnBoardingRenderer(actions)
              )
        )

        adapter.enableDiffUtil()

        listContent.layoutManager = LinearLayoutManager(this)
        listContent.itemAnimator = DefaultItemAnimator()

        listContent.adapter = adapter
    }

}