package de.lizsoft.travelcheck.onboarding.model

import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.model.common.Airport
import de.lizsoft.travelcheck.onboarding.TravelOnBoardingActivityPresenter

data class OnBoardingRendererDepartureModel(
      private val settingsProvider: SettingsProvider
) : OnBoardingRendererBaseModel {

    var userAirport: Airport = settingsProvider.getDepartureAirport()
    var selectedAirports: List<Airport> = settingsProvider.getDepartureFlexibleAirports()

    override fun getOnBoardingRendererModel(): OnBoardingModel = OnBoardingModel(
          name = "Departure",
          value = userAirport.toString(),
          isCompleted = userAirport != Airport.None,
          isFlexible = selectedAirports.isNotEmpty(),
          hasFlexibility = true,
          action = TravelOnBoardingActivityPresenter.Action.DepartureClicked,
          FlexibilityAction = TravelOnBoardingActivityPresenter.Action.DepartureFlexibilityClicked,
          FlexibilityRemovedAction = TravelOnBoardingActivityPresenter.Action.DepartureFlexibilityRemoved
    )
}