package de.lizsoft.travelcheck.dailycheck.renderer

import android.widget.TextView
import com.bandiago.libs.simplegraph.LineGraph
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewFinder
import de.lizsoft.travelcheck.R
import de.lizsoft.travelcheck.dailycheck.model.TravelCheckDailyModel

class TravelDailyCheckContentRenderer : ViewBinder.Binder<TravelCheckDailyModel> {

    override fun bindView(travelCheckDailyModel: TravelCheckDailyModel, finder: ViewFinder, payloads: MutableList<Any>) {
        finder.find<TextView>(R.id.travel_daily_check_city_name).text = "${travelCheckDailyModel.city.name} - " +
              "${travelCheckDailyModel.city.regionName}"

        finder.find<TextView>(R.id.travel_daily_check_min).text = "Min: " +
              "${travelCheckDailyModel.getMinPrice().priceModel.amount} " +
              "${travelCheckDailyModel.getMinPrice().priceModel.currency} " +
              "At " +
              "${travelCheckDailyModel.getMinPrice().date}"

        val chartData = travelCheckDailyModel.getTimePrices().map { it.price.amount.toFloat() }.toMutableList()
        if (chartData.size == 1) {
            chartData.add(0, 0f)
        }

        finder.find<LineGraph>(R.id.travel_daily_check_lineGraph).addSeries(
              chartData.toTypedArray()
        )
    }
}