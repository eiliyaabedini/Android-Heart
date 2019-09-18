package de.lizsoft.travelcheck.travelcheck.renderer

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewFinder
import com.sackcentury.shinebuttonlib.ShineButton
import de.lizsoft.heart.interfaces.common.LocalStorageManager
import de.lizsoft.heart.interfaces.common.model.HistoricalData
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.travelcheck.R
import de.lizsoft.travelcheck.model.common.CityTravelModel
import de.lizsoft.travelcheck.model.common.PriceTravelModel
import de.lizsoft.travelcheck.travelcheck.TravelCheckActivityPresenter
import io.reactivex.subjects.Subject
import timber.log.Timber

class TravelCheckContentCityRenderer(
      private val actions: Subject<PresenterAction>,
      private val localStorageManager: LocalStorageManager
) : ViewBinder.Binder<CityTravelModel> {

    override fun bindView(cityTravelModel: CityTravelModel, finder: ViewFinder, payloads: MutableList<Any>) {
        finder.find<TextView>(R.id.travel_check_city_name).text = cityTravelModel.name
        finder.find<TextView>(R.id.travel_check_region_name).text =
              "${cityTravelModel.country} - ${cityTravelModel.regionName}"
        finder.find<TextView>(R.id.travel_check_price).text =
              "${cityTravelModel.price.amount} ${cityTravelModel.price.currency}"
        finder.find<TextView>(R.id.travel_check_weather).text = cityTravelModel.weather.air
        finder.find<TextView>(R.id.travel_check_water).text = cityTravelModel.weather.water

        val previousPrices = localStorageManager.getHistoricalListByKey(
              key = cityTravelModel.getUniqueKey(),
              clazz = PriceTravelModel::class.java
        )?.let {
            if (it.size > 1) {
                it.dropLast(1)
            } else {
                it
            }
        }

        Timber.d("id:${cityTravelModel.getUniqueKey()} currPrice:${cityTravelModel.price.amount} previousPrices:${previousPrices?.joinToString()}")

        updateContents(cityTravelModel, previousPrices, finder)

        finder.find<ShineButton>(R.id.travel_check_favourite_icon).apply {
            setOnCheckStateChangeListener(null)
            isChecked = cityTravelModel.isFavourite
            setOnCheckStateChangeListener { _, checked ->
                Timber.d("setOnCheckedChangeListener id:${cityTravelModel.getUniqueKey()} checked:$checked")
                if (checked) {
                    actions.onNext(TravelCheckActivityPresenter.Action.ListContentFavourites(cityId = cityTravelModel.id))
                } else {
                    actions.onNext(TravelCheckActivityPresenter.Action.ListContentUnFavourites(cityId = cityTravelModel.id))
                }

                //As presenter is not updating the view to prevent flashing, View is updating itself here
                updateContents(cityTravelModel.copy(isFavourite = isChecked), previousPrices, finder)
            }
        }

        finder.find<View>(R.id.travel_check_root).setOnClickListener {
            Timber.d("city$cityTravelModel")
            actions.onNext(
                  TravelCheckActivityPresenter.Action.ListContentClicked(
                        repositoryType = cityTravelModel.repositoryType,
                        cityId = cityTravelModel.id
                  )
            )
        }

        finder.find<View>(R.id.travel_check_root).setOnLongClickListener {
            actions.onNext(
                  TravelCheckActivityPresenter.Action.ListContentLongClicked(
                        repositoryType = cityTravelModel.repositoryType,
                        cityId = cityTravelModel.id,
                        hotelId = cityTravelModel.hotelId
                  )
            )

            true
        }
    }

    private fun updateContents(cityTravelModel: CityTravelModel, previousPrices: List<HistoricalData<PriceTravelModel>>?, finder: ViewFinder) {
        if (!cityTravelModel.isFavourite || previousPrices.isNullOrEmpty()) {
            finder.find<TextView>(R.id.travel_check_price_difference).isVisible = false
            finder.find<TextView>(R.id.travel_check_price).setBackgroundColor(Color.WHITE)
        } else {
            finder.find<TextView>(R.id.travel_check_price_difference).apply {
                isVisible = true
                val previousPriceAmount: Double = previousPrices.last().model.amount
                text = previousPriceAmount.toString()

                when {
                    previousPriceAmount > cityTravelModel.price.amount -> //Price decreased
                        finder.find<TextView>(R.id.travel_check_price).setBackgroundColor(Color.GREEN)
                    previousPriceAmount < cityTravelModel.price.amount -> //Price increased
                        finder.find<TextView>(R.id.travel_check_price).setBackgroundColor(Color.RED)
                    else -> //Price is the same
                        finder.find<TextView>(R.id.travel_check_price).setBackgroundColor(Color.WHITE)
                }
            }
        }
    }

}