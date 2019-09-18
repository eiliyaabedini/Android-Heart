package de.lizsoft.heart.maptools.ui.search

import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterView
import de.lizsoft.heart.interfaces.map.Location
import de.lizsoft.heart.interfaces.model.SearchAddressPrediction

class SelectAddressOnMapPresenter(private val reactiveTransformer: ReactiveTransformer): Presenter<SelectAddressOnMapPresenter.View>() {

    private var selectedLocation: Location? = null

    override fun initialise() {
        commonView?.actions()
              ?.subscribeOn(reactiveTransformer.ioScheduler())
              ?.subscribeSafeWithShowingErrorContent { action ->
                  when (action) {
                      is Action.ConfirmButtonClicked -> {
                          confirmSelectedAddress()
                      }

                      is Action.SelectedLocationChanged -> {
                          selectedLocation = action.newLocation
                          view?.updateConfirmButtonState(selectedLocation != null)
                          view?.updateFetchedAddressText(selectedLocation?.address)
                      }

                      is Action.MapStartedMoving -> {
                          selectedLocation = null
                      }
                  }
              }
    }

    private fun confirmSelectedAddress() {
        val location = selectedLocation ?: return
        commonView?.closeScreenWithResult(
              SearchAddressPrediction(location, false)
        )
    }

    interface View : PresenterView {
        fun updateFetchedAddressText(address: String?)
        fun updateConfirmButtonState(isEnabled: Boolean)
    }

    sealed class Action : PresenterAction {
        object ConfirmButtonClicked : Action()
        object MapStartedMoving : Action()
        data class SelectedLocationChanged(val newLocation: Location?) : Action()
    }
}