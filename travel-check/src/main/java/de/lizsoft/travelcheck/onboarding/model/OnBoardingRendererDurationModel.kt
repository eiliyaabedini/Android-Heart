package de.lizsoft.travelcheck.onboarding.model

import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.onboarding.TravelOnBoardingActivityPresenter

data class OnBoardingRendererDurationModel(
      private val settingsProvider: SettingsProvider
) : OnBoardingRendererBaseModel {

    var duration: Int = settingsProvider.getDuration()

    override fun getOnBoardingRendererModel(): OnBoardingModel = OnBoardingModel(
          name = "Duration",
          value = duration.toString(),
          isCompleted = true,
          isFlexible = false, //TODO implement flexibility
          hasFlexibility = false,
          action = TravelOnBoardingActivityPresenter.Action.DurationClicked,
          FlexibilityAction = null,
          FlexibilityRemovedAction = null
    )
}