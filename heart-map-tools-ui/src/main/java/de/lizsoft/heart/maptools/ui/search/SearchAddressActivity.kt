package de.lizsoft.heart.maptools.ui.search

import android.view.View
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.jakewharton.rxbinding3.widget.textChanges
import com.mancj.materialsearchbar.MaterialSearchBar
import de.lizsoft.heart.interfaces.navigator.retrieveArgument
import de.lizsoft.heart.common.ui.ActivityWithPresenter
import de.lizsoft.heart.common.ui.ScreenBucket
import de.lizsoft.heart.common.ui.extension.bindView
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import de.lizsoft.heart.interfaces.map.model.SearchAddressLaunchModel
import de.lizsoft.heart.maptools.ui.R
import de.lizsoft.heart.maptools.ui.search.renderer.SearchItemRenderer
import de.lizsoft.heart.maptools.ui.search.renderer.SearchItemRendererCurrentLocation
import de.lizsoft.heart.maptools.ui.search.renderer.SearchItemRendererSelectOnMap
import de.lizsoft.heart.maptools.ui.search.renderer.model.SearchItemModel
import io.reactivex.rxkotlin.plusAssign
import org.koin.androidx.scope.currentScope
import java.util.concurrent.TimeUnit

class SearchAddressActivity : ActivityWithPresenter() {

    private var launchModelArgument: SearchAddressLaunchModel? = null

    override fun getCurrentScreenBucket(): ScreenBucket<*> {
        return object : ScreenBucket<SearchAddressPresenter.View>(
              ScreenBucketModel(
                    scope = currentScope,
                    viewLayout = R.layout.activity_search_address,
                    enableDisplayHomeAsUp = true
              )
        ) {
            override fun getPresenterView(): SearchAddressPresenter.View {
                return object : SearchAddressPresenter.View {
                    override fun getLaunchModelArgument(): SearchAddressLaunchModel? {
                        return launchModelArgument
                    }

                    override fun showLoading() {
                        runOnUiThread {
                            progressView.isGone = false
                        }
                    }

                    override fun hideLoading() {
                        runOnUiThread {
                            progressView.isGone = true
                        }
                    }

                    override fun showEmptyView() {
                        runOnUiThread {
                            emptyView.isGone = false
                        }
                    }

                    override fun hideEmptyView() {
                        runOnUiThread {
                            emptyView.isGone = true
                        }
                    }

                    override fun setItems(items: List<SearchItemModel>) {
                        runOnUiThread {
                            adapter.setItems(items)
                        }
                    }

                    override fun setDefaultAddress(defaultAddress: String) {
                        searchView.text = defaultAddress
                    }
                }
            }
        }
    }

    private val searchView: MaterialSearchBar by bindView(R.id.search_address_search_view)
    private val list: RecyclerView by bindView(R.id.search_address_list_recycler_view)
    private val progressView: View by bindView(R.id.search_address_search_loading)
    private val emptyView: View by bindView(R.id.search_address_search_empty)

    private val adapter = RendererRecyclerViewAdapter()

    override fun initializeBeforePresenter() {
        supportPostponeEnterTransition()
        launchModelArgument = retrieveArgument()
    }

    override fun initializeViewListeners() {

        enableSmartElevationForActionBar(list)

        searchView.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onButtonClicked(buttonCode: Int) {
                when (buttonCode) {
                    MaterialSearchBar.BUTTON_BACK -> finish()
                }
            }

            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    finish()
                }
            }

            override fun onSearchConfirmed(text: CharSequence) {
                actions.onNext(SearchAddressPresenter.Action.SearchTextChanged(text.toString()))
            }

        })

        disposables += searchView.searchEditText.textChanges()
              .debounce(500, TimeUnit.MILLISECONDS)
              .subscribe { text ->
                  actions.onNext(SearchAddressPresenter.Action.SearchTextChanged(text.toString()))
              }

        adapter.registerRenderer(
              ViewBinder(
                    R.layout.activity_search_items_row,
                    SearchItemModel.SearchItemModelPrediction::class.java,
                    SearchItemRenderer(actions)
              )
        )

        adapter.registerRenderer(
              ViewBinder(
                    R.layout.activity_search_items_row,
                    SearchItemModel.SearchItemModelCurrentLocation::class.java,
                    SearchItemRendererCurrentLocation(actions)
              )
        )

        adapter.registerRenderer(
              ViewBinder(
                    R.layout.activity_search_items_row,
                    SearchItemModel.SearchItemModelSelectOnMap::class.java,
                    SearchItemRendererSelectOnMap(actions)
              )
        )

        adapter.enableDiffUtil()

        list.layoutManager = LinearLayoutManager(this)

        list.adapter = adapter

        searchView.enableSearch()

        supportStartPostponedEnterTransition()
    }
}
