package de.lizsoft.travelcheck.onboarding.renderer

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewFinder
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.travelcheck.R
import de.lizsoft.travelcheck.onboarding.model.OnBoardingModel
import io.reactivex.subjects.Subject

class TravelCheckOnBoardingRenderer(
      private val actions: Subject<PresenterAction>
) : ViewBinder.Binder<OnBoardingModel> {

    override fun bindView(onBoardingModel: OnBoardingModel, finder: ViewFinder, payloads: MutableList<Any>) {
        finder.find<TextView>(R.id.travel_check_onboarding_name).text = onBoardingModel.name
        finder.find<TextView>(R.id.travel_check_onboarding_value).text = onBoardingModel.value
        finder.find<AppCompatImageView>(R.id.travel_check_onboarding_completed).setImageResource(
              if (onBoardingModel.isCompleted) R.drawable.ic_check_circle_black_24dp else R.drawable.ic_circle_outline_black_24dp
        )

        finder.find<View>(R.id.travel_check_onboarding_root).setOnClickListener {
            actions.onNext(onBoardingModel.action)
        }

//        finder.find<AppCompatImageView>(R.id.travel_check_onboarding_flexibility).apply {
//
//            if (onBoardingModel.hasFlexibility) {
//                visibility = View.VISIBLE
//
//                setImageResource(if (onBoardingModel.isFlexible) R.drawable.ic_star_black_24dp else R.drawable.ic_star_border_black_24dp)
//
//                setOnClickListener {
//                    if (onBoardingModel.isFlexible) {
//                        onBoardingModel.FlexibilityRemovedAction?.let {
//                            actions.onNext(onBoardingModel.FlexibilityRemovedAction)
//                        }
//                    } else {
//                        onBoardingModel.FlexibilityAction?.let {
//                            actions.onNext(onBoardingModel.FlexibilityAction)
//                        }
//                    }
//                }
//            } else {
//                visibility = View.INVISIBLE
//
//                setOnClickListener {}
//            }
//
//        }
    }
}