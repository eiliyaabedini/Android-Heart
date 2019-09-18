package de.lizsoft.travelcheck.onboarding.model

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.travelcheck.onboarding.TravelOnBoardingActivityPresenter

data class OnBoardingModel(
      val name: String,
      val value: String,
      val isCompleted: Boolean,
      val isFlexible: Boolean = false,
      val hasFlexibility: Boolean = false,
      val action: TravelOnBoardingActivityPresenter.Action,
      val FlexibilityAction: TravelOnBoardingActivityPresenter.Action? = null,
      val FlexibilityRemovedAction: TravelOnBoardingActivityPresenter.Action? = null
) : ViewModel