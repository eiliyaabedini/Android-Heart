package de.lizsoft.travelcheck.dailycheck

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import de.lizsoft.heart.common.ui.ActivityWithPresenter
import de.lizsoft.heart.common.ui.ScreenBucket
import de.lizsoft.heart.common.ui.extension.bindView
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import de.lizsoft.travelcheck.R
import de.lizsoft.travelcheck.dailycheck.model.TravelCheckDailyModel
import de.lizsoft.travelcheck.dailycheck.renderer.TravelDailyCheckContentRenderer
import org.koin.androidx.scope.currentScope

class TravelDailyCheckActivity : ActivityWithPresenter() {
    override fun getCurrentScreenBucket(): ScreenBucket<*> {
        return object : ScreenBucket<TravelDailyCheckActivityPresenter.View>(
              ScreenBucketModel(
                    scope = currentScope,
                    viewLayout = R.layout.activity_travel_daily_check,
                    enableDisplayHomeAsUp = true
              )
        ) {
            override fun getPresenterView(): TravelDailyCheckActivityPresenter.View {
                return object : TravelDailyCheckActivityPresenter.View {
                    override fun showCounter(allCount: Int, fetchedCount: Int) {
                        runOnUiThread {
                            txtCounter.text = "$fetchedCount/$allCount"
                        }
                    }

                    override fun showItems(items: List<ViewModel>) {
                        runOnUiThread {
                            adapter.setItems(items)
                        }
                    }

                }
            }
        }
    }

    private val btnStart: View by bindView(R.id.travel_daily_check_start_button)
    private val btnStop: View by bindView(R.id.travel_daily_check_stop_button)
    private val txtCounter: TextView by bindView(R.id.travel_daily_check_counter_text)
    private val inputDays: TextView by bindView(R.id.travel_daily_check_days_input)
    private val listContent: RecyclerView by bindView(R.id.travel_daily_check_content_list)

    private val adapter = RendererRecyclerViewAdapter()

    override fun initializeViewListeners() {
        adapter.registerRenderer(
              ViewBinder(
                    R.layout.travel_daily_check_content_renderer,
                    TravelCheckDailyModel::class.java,
                    TravelDailyCheckContentRenderer()
              )
        )

        adapter.enableDiffUtil()

        listContent.layoutManager = LinearLayoutManager(this)
        listContent.itemAnimator = DefaultItemAnimator()

        listContent.adapter = adapter

        btnStart.setOnClickListener {
            actions.onNext(TravelDailyCheckActivityPresenter.Action.StartButtonClicked(inputDays.text.toString().toInt()))
        }

        btnStop.setOnClickListener {
            actions.onNext(TravelDailyCheckActivityPresenter.Action.StopButtonClicked)
        }
    }
}