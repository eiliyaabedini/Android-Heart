package de.lizsoft.travelcheck.travelcheck

import android.view.View
import android.widget.ToggleButton
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import de.lizsoft.heart.common.ui.ActivityWithPresenter
import de.lizsoft.heart.common.ui.ScreenBucket
import de.lizsoft.heart.common.ui.extension.bindView
import de.lizsoft.heart.common.ui.ui.bottomsheet.TextBottomSheet
import de.lizsoft.heart.common.ui.ui.bottomsheet.model.TextBottomSheetModel
import de.lizsoft.heart.interfaces.common.LocalStorageManager
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import de.lizsoft.travelcheck.R
import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.model.common.CityTravelModel
import de.lizsoft.travelcheck.model.common.RegionTravelModel
import de.lizsoft.travelcheck.travelcheck.renderer.TravelCheckContentCityRenderer
import de.lizsoft.travelcheck.travelcheck.renderer.TravelCheckContentRegionRenderer
import de.lizsoft.travelcheck.workmanager.TravelCheckWorkManagers
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.currentScope

class TravelCheckActivity : ActivityWithPresenter() {

    override fun getCurrentScreenBucket(): ScreenBucket<*> {
        return object : ScreenBucket<TravelCheckActivityPresenter.View>(
              ScreenBucketModel(
                    scope = currentScope,
                    viewLayout = R.layout.activity_travel_check,
                    enableDisplayHomeAsUp = true
              )
        ) {
            override fun getPresenterView(): TravelCheckActivityPresenter.View {
                return object : TravelCheckActivityPresenter.View {
                    override fun showContents(items: List<ViewModel>, notifyAdapter: Boolean) {
                        runOnUiThread {
                            if (notifyAdapter.not()) {
                                adapter.disableDiffUtil()
                            }

                            adapter.setItems(items)

                            if (notifyAdapter.not()) {
                                adapter.enableDiffUtil()
                            }

                            swipeRefreshLayout.isRefreshing = false
                        }
                    }

                    override fun showSettings() {
                        runOnUiThread {
                            TextBottomSheet.showTexts(
                                  supportFragmentManager = supportFragmentManager,
                                  texts = listOf(
                                        TextBottomSheetModel(
                                              text = "Auto Check: ${TravelCheckWorkManagers.isRoutineAutoCheckRunning()}",
                                              icon = R.drawable.ic_edit_black_24dp,
                                              clickCallback = { delegate ->
                                                  delegate.dismiss()
                                                  actions.onNext(
                                                        TravelCheckActivityPresenter.Action.SettingsAutoCheckClicked
                                                  )
                                              }
                                        ),
                                        TextBottomSheetModel(
                                              text = "Check Flexibility: ${settingsProvider.getFlexibilityFeatureEnable()}",
                                              icon = R.drawable.ic_edit_black_24dp,
                                              clickCallback = { delegate ->
                                                  delegate.dismiss()
                                                  actions.onNext(
                                                        TravelCheckActivityPresenter.Action.SettingsFlexibilityEnabledClicked
                                                  )
                                              }
                                        ),
                                        TextBottomSheetModel(
                                              text = "Daily check",
                                              icon = R.drawable.ic_edit_black_24dp,
                                              clickCallback = { delegate ->
                                                  delegate.dismiss()
                                                  actions.onNext(TravelCheckActivityPresenter.Action.DailyCheckButtonClicked)
                                              }
                                        )
                                  ),
                                  title = "Settings"
                            )
                        }
                    }
                }
            }
        }
    }

    private val btnFavourite: ToggleButton by bindView(R.id.travel_check_fav_button)
    private val btnSort: View by bindView(R.id.travel_check_sort_button)
    private val btnSettings: View by bindView(R.id.travel_check_settings_button)
    private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.travel_check_content_list_swipe_refresh)
    private val listContent: RecyclerView by bindView(R.id.travel_check_content_list)

    private val localStorageManager: LocalStorageManager by inject()
    private val settingsProvider: SettingsProvider by inject()

    private val adapter = RendererRecyclerViewAdapter()

    override fun initializeViewListeners() {
        enableSmartElevationForActionBar(listContent)

        btnFavourite.setOnCheckedChangeListener { _, isChecked ->
            actions.onNext(TravelCheckActivityPresenter.Action.ToggleFavouritesButtonClicked(isChecked))
        }

        swipeRefreshLayout.setOnRefreshListener {
            actions.onNext(TravelCheckActivityPresenter.Action.PullToRefreshRefreshCalled)
        }

        btnSort.setOnClickListener {
            actions.onNext(TravelCheckActivityPresenter.Action.SortButtonClicked)
        }

        btnSettings.setOnClickListener {
            actions.onNext(TravelCheckActivityPresenter.Action.SettingsButtonClicked)
        }

        btnSettings.setOnLongClickListener {
            actions.onNext(TravelCheckActivityPresenter.Action.SettingsButtonLongClicked)
            true
        }

        adapter.registerRenderer(
              ViewBinder(
                    R.layout.travel_check_content_region_renderer,
                    RegionTravelModel::class.java,
                    TravelCheckContentRegionRenderer()
              )
        )

        adapter.registerRenderer(
              ViewBinder(
                    R.layout.travel_check_content_city_renderer,
                    CityTravelModel::class.java,
                    TravelCheckContentCityRenderer(actions, localStorageManager)
              )
        )

        adapter.enableDiffUtil()

        listContent.layoutManager = LinearLayoutManager(this)
        listContent.itemAnimator = DefaultItemAnimator()

        listContent.adapter = adapter
    }

    override fun getPullToRefreshLayout(): SwipeRefreshLayout? = swipeRefreshLayout
}