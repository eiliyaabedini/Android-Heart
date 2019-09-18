package de.lizsoft.travelcheck.onboarding.model

import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.onboarding.TravelOnBoardingActivityPresenter

data class OnBoardingRendererChildrenAgesModel(
      private val settingsProvider: SettingsProvider
) : OnBoardingRendererBaseModel {

    var childrenAges: MutableList<Int> = mutableListOf(settingsProvider.getChildAge())

    override fun getOnBoardingRendererModel(): OnBoardingModel = OnBoardingModel(
          name = "Age of Children",
          value = childrenAges.map { if (it == -1) "None" else it.toString() }.joinToString(", "),
          isCompleted = true,
          action = TravelOnBoardingActivityPresenter.Action.ChildAgeClicked
    )
}