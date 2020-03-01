package de.lizsoft.travelcheck.travelcheck

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterView
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import de.lizsoft.travelcheck.BuildConfig
import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.model.OpenTravelDailyCheckScreen
import de.lizsoft.travelcheck.model.OpenTravelOnBoardingScreen
import de.lizsoft.travelcheck.model.common.SortType
import de.lizsoft.travelcheck.repository.RepositoryType
import de.lizsoft.travelcheck.repository.TravelMainRepository
import de.lizsoft.travelcheck.workmanager.TravelCheckWorkManagers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TravelCheckActivityPresenter(
    private val mainTravelRepository: TravelMainRepository,
    private val settingsProvider: SettingsProvider,
    private val heartNavigator: HeartNavigator,
    private val reactiveTransformer: ReactiveTransformer
) : Presenter<TravelCheckActivityPresenter.View>() {

    private var isFilterFavourites: Boolean = false

    override fun initialise() {

        commonView?.actions()
              ?.subscribeOn(reactiveTransformer.ioScheduler())
              ?.subscribeSafeWithShowingErrorContent { action ->
                  Timber.d("action: $action")
                  when (action) {
                      is PresenterCommonAction.ErrorRetryClicked -> {
                          commonView?.showContent()
                      }

                      is Action.ToggleFavouritesButtonClicked -> {
                          isFilterFavourites = action.isFavSelected
                          updateList()
                      }

                      is Action.PullToRefreshRefreshCalled -> {
                          updateList(true)
                      }

                      is Action.DailyCheckButtonClicked -> {
                          heartNavigator.getLauncher(OpenTravelDailyCheckScreen)?.startActivity()
                      }

                      is Action.ListContentFavourites -> {
                          settingsProvider.addFavouriteCity(action.cityId)

                          updateList(notifyAdapter = false)
                      }

                      is Action.ListContentUnFavourites -> {
                          settingsProvider.removeFavouriteCity(action.cityId)

                          updateList(notifyAdapter = false)
                      }

                      is Action.ListContentClicked -> {
                          commonView?.openUrl(
                                mainTravelRepository.getCityOfferUrl(
                                      action.repositoryType,
                                      action.cityId.toString()
                                )
                          )
                      }

                      is Action.ListContentLongClicked -> {
                          fetchAndValidateOffers(
                                repositoryType = action.repositoryType,
                                numberOfOffersToValidate = 1,
                                cityId = action.cityId,
                                hotelId = action.hotelId
                          )
                      }

                      is Action.SettingsButtonClicked -> {
                          if (BuildConfig.DEBUG) {
                              heartNavigator.getLauncher(
                                    OpenTravelOnBoardingScreen
                              )?.startActivity()
                          }
                      }

                      is Action.SettingsButtonLongClicked -> {
                          view?.showSettings()
                      }

                      is Action.SortButtonClicked -> {
                          commonView?.makeItemsDialog(
                                items = *arrayOf(
                                      "None" to { settingsProvider.setSortType(SortType.NONE) },
                                      "Lowest Price" to { settingsProvider.setSortType(SortType.PRICE) },
                                      "Warmest Water" to { settingsProvider.setSortType(SortType.WATER_TEMPERATURE) },
                                      "Hottest Weather" to { settingsProvider.setSortType(SortType.WEATHER_TEMPERATURE) }
                                )
                          ) {
                              updateList()
                          }
                      }

                      is Action.SettingsAutoCheckClicked -> {
                          if (TravelCheckWorkManagers.isRoutineAutoCheckRunning()) {
                              TravelCheckWorkManagers.cancelRoutineAutoCheckRunning()
                          } else {

                              commonView?.makeItemsDialog(
                                    items = *arrayOf(
                                          "100 EUR" to { settingsProvider.setRoutineMaxPrice(100) },
                                          "150 EUR" to { settingsProvider.setRoutineMaxPrice(150) },
                                          "200 EUR" to { settingsProvider.setRoutineMaxPrice(200) },
                                          "250 EUR" to { settingsProvider.setRoutineMaxPrice(250) },
                                          "300 EUR" to { settingsProvider.setRoutineMaxPrice(300) },
                                          "350 EUR" to { settingsProvider.setRoutineMaxPrice(350) },
                                          "500 EUR" to { settingsProvider.setRoutineMaxPrice(500) },
                                          "750 EUR" to { settingsProvider.setRoutineMaxPrice(750) },
                                          "1000 EUR" to { settingsProvider.setRoutineMaxPrice(1000) }
                                    )
                              ) {
                                  commonView?.makeItemsDialog(
                                        items = *arrayOf(
                                              "Every 15 Min" to {
                                                  TravelCheckWorkManagers.runRoutineAutoCheck(
                                                        15,
                                                        TimeUnit.MINUTES
                                                  )
                                              },
                                              "Every 30 Min" to {
                                                  TravelCheckWorkManagers.runRoutineAutoCheck(
                                                        30,
                                                        TimeUnit.MINUTES
                                                  )
                                              },
                                              "Every 1 Hours" to {
                                                  TravelCheckWorkManagers.runRoutineAutoCheck(
                                                        1,
                                                        TimeUnit.HOURS
                                                  )
                                              },
                                              "Every 3 Hours" to {
                                                  TravelCheckWorkManagers.runRoutineAutoCheck(
                                                        3,
                                                        TimeUnit.HOURS
                                                  )
                                              },
                                              "Every 6 Hours" to {
                                                  TravelCheckWorkManagers.runRoutineAutoCheck(
                                                        6,
                                                        TimeUnit.HOURS
                                                  )
                                              },
                                              "Every 12 Hours" to {
                                                  TravelCheckWorkManagers.runRoutineAutoCheck(
                                                        12,
                                                        TimeUnit.HOURS
                                                  )
                                              },
                                              "Daily" to { TravelCheckWorkManagers.runRoutineAutoCheck(1, TimeUnit.DAYS) }
                                        )
                                  )
                              }
                          }
                      }

                      is Action.SettingsFlexibilityEnabledClicked -> {
                          commonView?.makeItemsDialog(
                                items = *arrayOf(
                                      "Turn On" to { settingsProvider.setFlexibilityFeatureEnable(true) },
                                      "Turn Off" to { settingsProvider.setFlexibilityFeatureEnable(false) }
                                )
                          )
                      }
                  }
              }

        updateList(false)
    }

    private fun updateList(forceUpdate: Boolean = false, notifyAdapter: Boolean = true) {
        commonView?.showContentLoading()

        mainTravelRepository.getAllDestinations(
              forceUpdate,
              isFilterFavourites,
              settingsProvider.getFlexibilityFeatureEnable()
        )
              .doOnSuccess { items ->
                  view?.showContents(items, notifyAdapter)

                  commonView?.showContent()
              }
              .subscribeSafeWithShowingErrorContent()
    }

    private fun fetchAndValidateOffers(
        repositoryType: RepositoryType,
        numberOfOffersToValidate: Int,
        cityId: Int,
        hotelId: Int
    ) {
        commonView?.showContentLoading()

        mainTravelRepository.getHotelOffers(
              repositoryType = repositoryType,
              cityId = cityId,
              hotelId = hotelId
        )
              .flattenAsFlowable { it.take(numberOfOffersToValidate) }
              .flatMapSingle { hotelOffer ->
                  mainTravelRepository.getHotelOfferValidation(
                        repositoryType = repositoryType,
                        offerId = hotelOffer.id,
                        cityId = cityId,
                        hotelId = hotelId
                  )
              }
              .doOnNext { offerValidation ->
                  Timber.d("$offerValidation")
                  commonView?.showSnackBar("Total price:${offerValidation.totalPrice.amount} ${offerValidation.totalPrice.currency}")
              }
              .doOnComplete {
                  commonView?.showContent()
              }
              .doOnError { it.printStackTrace() }
              .subscribe()
    }

    interface View : PresenterView {
        fun showContents(items: List<ViewModel>, notifyAdapter: Boolean)

        fun showSettings()
    }

    sealed class Action : PresenterAction {
        data class ToggleFavouritesButtonClicked(val isFavSelected: Boolean) : Action()
        object PullToRefreshRefreshCalled : Action()
        object DailyCheckButtonClicked : Action()
        object SortButtonClicked : Action()
        data class ListContentClicked(val repositoryType: RepositoryType, val cityId: Int) : Action()
        data class ListContentFavourites(val cityId: Int) : Action()
        data class ListContentUnFavourites(val cityId: Int) : Action()
        data class ListContentLongClicked(val repositoryType: RepositoryType, val cityId: Int, val hotelId: Int) : Action()

        object SettingsButtonClicked : Action()
        object SettingsButtonLongClicked : Action()
        object SettingsAutoCheckClicked : Action()
        object SettingsFlexibilityEnabledClicked : Action()
    }

}
