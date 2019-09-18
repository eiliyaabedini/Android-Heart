package de.lizsoft.travelcheck.onboarding.model

import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.onboarding.TravelOnBoardingActivityPresenter

data class OnBoardingRendererStartDateModel(
      private val settingsProvider: SettingsProvider
) : OnBoardingRendererBaseModel {

    var date: String? = settingsProvider.getStartDate()

    override fun getOnBoardingRendererModel(): OnBoardingModel = OnBoardingModel(
          name = "Start Date",
          value = date ?: "",
          isCompleted = date != null,
          action = TravelOnBoardingActivityPresenter.Action.StartDateClicked
    )
}