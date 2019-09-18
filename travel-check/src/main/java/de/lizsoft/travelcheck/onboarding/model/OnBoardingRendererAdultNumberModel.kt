package de.lizsoft.travelcheck.onboarding.model

import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.onboarding.TravelOnBoardingActivityPresenter

data class OnBoardingRendererAdultNumberModel(
      private val settingsProvider: SettingsProvider
) : OnBoardingRendererBaseModel {

    var adultsCount: Int = settingsProvider.getNumberOfAdults()

    override fun getOnBoardingRendererModel(): OnBoardingModel = OnBoardingModel(
          name = "Number of Adults",
          value = adultsCount.toString(),
          isCompleted = true,
          action = TravelOnBoardingActivityPresenter.Action.NumberOfAdultsClicked
    )
}