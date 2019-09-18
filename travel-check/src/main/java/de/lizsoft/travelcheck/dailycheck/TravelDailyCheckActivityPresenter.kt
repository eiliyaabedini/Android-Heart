package de.lizsoft.travelcheck.dailycheck

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.heart.common.extension.addDays
import de.lizsoft.heart.common.extension.toDateWithFormat
import de.lizsoft.heart.common.extension.toStringWithFormat
import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.common.LocalStorageManager
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterView
import de.lizsoft.travelcheck.dailycheck.model.TravelCheckDailyModel
import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.model.common.CityTravelModel
import de.lizsoft.travelcheck.service.TravelCheckLastMinutesApi
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.util.*

class TravelDailyCheckActivityPresenter(
      private val travelCheckLastMinutesApi: TravelCheckLastMinutesApi,
      private val settingsProvider: SettingsProvider,
      private val localStorageManager: LocalStorageManager,
      private val reactiveTransformer: ReactiveTransformer
) : Presenter<TravelDailyCheckActivityPresenter.View>() {

    private val allItems: MutableMap<Int, TravelCheckDailyModel> = mutableMapOf()

    override fun initialise() {

        loadCachedData()

        commonView?.actions()
              ?.subscribeOn(reactiveTransformer.ioScheduler())
              ?.subscribeSafeWithShowingErrorContent { action ->
                  Timber.d("action: $action")
                  when (action) {
                      is PresenterCommonAction.ErrorRetryClicked -> {
                          commonView?.showContent()
                      }

                      is Action.StartButtonClicked -> {
                          commonView?.showContentLoading()

                          val startDate: String = settingsProvider.getStartDate()

                          fetchPrice(startDate, action.days)
                                .map {
                                    view?.showCounter(action.days, it)
                                }
                                .subscribeOn(reactiveTransformer.ioScheduler())
                                .doOnComplete {
                                    cacheData()

                                    showItems()
                                }
                                .subscribeSafeWithShowingErrorContent()
                      }

                      is Action.StopButtonClicked -> {

                      }
                  }
              }
    }

    private fun showItems() {
        view?.showItems(
              allItems.values
                    .toList()
                    .sortedBy { it.getMinPrice().priceModel.amount }
        )
        commonView?.showContent()
    }

    private fun cacheData() {
        localStorageManager.setListsByKey(
              "TravelDailyCheckActivityPresenter_items",
              allItems.values.toList(),
              TravelCheckDailyModel::class.java
        )
    }

    private fun loadCachedData() {
        localStorageManager.getListByKey(
              "TravelDailyCheckActivityPresenter_items",
              TravelCheckDailyModel::class.java
        )?.let {
            it.forEach { cachedItem ->
                allItems[cachedItem.city.id] = cachedItem
            }

            showItems()
        }
    }

    private fun fetchPrice(startDateStr: String, days: Int): Observable<Int> {
        var fetched: Int = 0

        view?.showCounter(days, fetched)

        val startDate: Date = startDateStr.toDateWithFormat(SettingsProvider.DATE_FORMAT)


        return Observable.range(0, days)
              .map { index ->
                  startDate
                        .addDays(index)
                        .toStringWithFormat(SettingsProvider.DATE_FORMAT)
              }
              .flatMapSingle { currentDate ->
                  checkPricesInADate(
                        startDate = currentDate,
                        endDate = currentDate
                              .toDateWithFormat(SettingsProvider.DATE_FORMAT)
                              .addDays(settingsProvider.getDuration() + 1)
                              .toStringWithFormat(SettingsProvider.DATE_FORMAT)
                  )
                        .doOnSuccess { cities ->
                            cities.forEach { city ->
                                if (allItems.containsKey(city.id).not()) {
                                    allItems[city.id] = TravelCheckDailyModel(city = city)
                                }

                                allItems[city.id]?.addPrice(
                                      date = currentDate,
                                      price = city.price
                                )
                            }
                        }
              }
              .map {
                  ++fetched
              }

    }

    private fun checkPricesInADate(startDate: String, endDate: String): Single<List<CityTravelModel>> {
        Timber.d("checkPricesInADate startDate:$startDate, endDate:$endDate")
        return travelCheckLastMinutesApi.getAllDestinationsInfo(
              startDate = startDate,
              endDate = endDate,
              minStars = settingsProvider.getStars(),
              departureAirportCode = settingsProvider.getDepartureAirport().code,
              adultsCount = settingsProvider.getNumberOfAdults(),
              childAge = settingsProvider.getChildAge(),
              durationDays = settingsProvider.getDuration()
        )
              .map { it.convert(emptyList()) }
              .map { regions -> regions.map { region -> region.cities }.flatten() }
    }

    interface View : PresenterView {
        fun showCounter(allCount: Int, fetchedCount: Int)
        fun showItems(items: List<ViewModel>)
    }

    sealed class Action : PresenterAction {
        data class StartButtonClicked(val days: Int) : Action()
        object StopButtonClicked : Action()
    }
}