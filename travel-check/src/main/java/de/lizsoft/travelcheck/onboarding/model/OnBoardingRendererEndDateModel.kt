package de.lizsoft.travelcheck.onboarding.model

import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.onboarding.TravelOnBoardingActivityPresenter

data class OnBoardingRendererEndDateModel(
      private val settingsProvider: SettingsProvider
) : OnBoardingRendererBaseModel {

    var date: String? = settingsProvider.getEndDate()

    override fun getOnBoardingRendererModel(): OnBoardingModel = OnBoardingModel(
          name = "End Date",
          value = date ?: "",
          isCompleted = date != null,
          action = TravelOnBoardingActivityPresenter.Action.EndDateClicked
    )
}