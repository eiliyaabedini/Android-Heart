package de.lizsoft.travelcheck.onboarding

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.heart.common.extension.getDateFormatUTC
import de.lizsoft.heart.common.extension.toDateWithFormat
import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterView
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.model.common.Airport
import de.lizsoft.travelcheck.onboarding.model.*
import timber.log.Timber

class TravelOnBoardingActivityPresenter(
      private val heartNavigator: HeartNavigator,
      private val settingsProvider: SettingsProvider,
      private val reactiveTransformer: ReactiveTransformer
) : Presenter<TravelOnBoardingActivityPresenter.View>() {

    private val items: List<OnBoardingRendererBaseModel> = listOf(
          OnBoardingRendererDepartureModel(settingsProvider),
          OnBoardingRendererStartDateModel(settingsProvider),
          OnBoardingRendererEndDateModel(settingsProvider),
          OnBoardingRendererAdultNumberModel(settingsProvider),
          OnBoardingRendererChildrenAgesModel(settingsProvider),
          OnBoardingRendererDurationModel(settingsProvider)
    )

    override fun initialise() {
        commonView?.actions()
              ?.subscribeOn(reactiveTransformer.ioScheduler())
              ?.subscribeSafeWithShowingErrorContent { action ->
                  Timber.d("action: $action")
                  when (action) {
                      is PresenterCommonAction.ErrorRetryClicked -> {
                          commonView?.showContent()
                      }

                      is Action.DepartureClicked -> {
                          commonView?.makeItemsDialog(
                                items = *Airport.values()
                                      .map { airport ->
                                          airport.toString() to {

                                              settingsProvider.setDepartureAirport(airport)

                                              items.filterIsInstance<OnBoardingRendererDepartureModel>()
                                                    .first().userAirport = airport

                                          }
                                      }.toTypedArray()
                          ) {
                              updateList()
                          }
                      }

                      is Action.DepartureFlexibilityClicked -> {
                          commonView?.makeMultiSelectItemsDialog(
                                items = *Airport.values()
                                      .filter { airport ->
                                          val model = items.filterIsInstance<OnBoardingRendererDepartureModel>()
                                                .first()
                                          airport != model.userAirport && airport != Airport.None
                                      }
                                      .map { airport ->
                                          val model = items.filterIsInstance<OnBoardingRendererDepartureModel>()
                                                .first()

                                          airport.toString() to
                                                model.selectedAirports.contains(airport)

                                      }.toTypedArray()
                          ) { airportsNames ->

                              val selectedAirports: List<Airport> = airportsNames.map { airportName ->
                                  Airport.valueOf(airportName)
                              }
                              settingsProvider.setDepartureFlexibleAirports(selectedAirports)

                              items.filterIsInstance<OnBoardingRendererDepartureModel>()
                                    .first()
                                    .selectedAirports = selectedAirports

                              updateList()
                          }
                      }

                      is Action.DepartureFlexibilityRemoved -> {
                          items.filterIsInstance<OnBoardingRendererDepartureModel>()
                                .first().selectedAirports = emptyList()

                          updateList()
                      }

                      is Action.StartDateClicked -> {
                          commonView?.showDialogDatePicker(
                                settingsProvider.getStartDate().toDateWithFormat(SettingsProvider.DATE_FORMAT),
                                false
                          )
                                ?.doOnSuccess { date ->
                                    settingsProvider.setStartDate(date)

                                    items.filterIsInstance<OnBoardingRendererStartDateModel>()
                                          .first().date = getDateFormatUTC(SettingsProvider.DATE_FORMAT).format(date)

                                    updateList()
                                }
                                ?.subscribeSafeWithShowingErrorContent()
                      }

                      is Action.EndDateClicked -> {
                          commonView?.showDialogDatePicker(
                                settingsProvider.getEndDate()?.toDateWithFormat(SettingsProvider.DATE_FORMAT),
                                false
                          )
                                ?.doOnSuccess { date ->
                                    settingsProvider.setEndDate(date)

                                    items.filterIsInstance<OnBoardingRendererEndDateModel>()
                                          .first().date = getDateFormatUTC(SettingsProvider.DATE_FORMAT).format(date)

                                    updateList()
                                }
                                ?.subscribeSafeWithShowingErrorContent()
                      }

                      is Action.NumberOfAdultsClicked -> {
                          commonView?.makeItemsDialog(
                                items = *(1 until 5).map { number ->
                                    number.toString() to {
                                        settingsProvider.setNumberOfAdults(number)
                                        items.filterIsInstance<OnBoardingRendererAdultNumberModel>()
                                              .first().adultsCount = number
                                    }
                                }.toTypedArray()
                          ) {
                              updateList()
                          }
                      }

                      is Action.ChildAgeClicked -> {
                          commonView?.makeItemsDialog(
                                items = *(1 until 18).map { number ->
                                    number.toString() to {
                                        settingsProvider.setChildAge(number)
                                        items.filterIsInstance<OnBoardingRendererChildrenAgesModel>()
                                              .first().childrenAges = mutableListOf(number)
                                    }
                                }.toTypedArray()
                          ) {
                              updateList()
                          }
                      }

                      is Action.DurationClicked -> {
                          commonView?.makeItemsDialog(
                                items = *(1 until 12).map { number ->
                                    number.toString() to {
                                        settingsProvider.setDuration(number)
                                        items.filterIsInstance<OnBoardingRendererDurationModel>()
                                              .first().duration = number
                                    }
                                }.toTypedArray()
                          ) {
                              updateList()
                          }
                      }
                  }
              }

        updateList()
    }

    private fun updateList() {
        view?.showItems(
              items.map { it.getOnBoardingRendererModel() }.sortedBy { it.isCompleted }
        )
    }

    interface View : PresenterView {
        fun showItems(items: List<ViewModel>)
    }

    sealed class Action : PresenterAction {
        object DepartureClicked : Action()
        object DepartureFlexibilityClicked : Action()
        object DepartureFlexibilityRemoved : Action()
        object StartDateClicked : Action()
        object EndDateClicked : Action()
        object NumberOfAdultsClicked : Action()
        object ChildAgeClicked : Action()
        object DurationClicked : Action()
    }
}