package de.lizsoft.heart.maptools.ui.search

import androidx.core.app.ActivityCompat.startActivityForResult
import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterView
import de.lizsoft.heart.interfaces.common.rx.doOnData
import de.lizsoft.heart.interfaces.common.rx.mapData
import de.lizsoft.heart.interfaces.map.model.SearchAddressLaunchModel
import de.lizsoft.heart.interfaces.map.service.AddressService
import de.lizsoft.heart.interfaces.map.service.CurrentLocation
import de.lizsoft.heart.interfaces.model.SearchAddressPrediction
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import de.lizsoft.heart.maptools.ui.navigation.OpenSelectAddressOnMap
import de.lizsoft.heart.maptools.ui.search.renderer.model.SearchItemModel
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class SearchAddressPresenter(
      private val currentLocation: CurrentLocation,
      private val addressService: AddressService,
      private val heartNavigator: HeartNavigator,
      private val reactiveTransformer: ReactiveTransformer
) : Presenter<SearchAddressPresenter.View>() {

    private val fetchAddressSubject = PublishSubject.create<String>()

    override fun initialise() {

        val initialItems = mutableListOf<SearchItemModel>()
        if (view?.getLaunchModelArgument()?.showCurrentLocationRow == true) {
            initialItems.add(SearchItemModel.SearchItemModelCurrentLocation)
        }

        if (view?.getLaunchModelArgument()?.showSelectOnMapRow == true) {
            initialItems.add(SearchItemModel.SearchItemModelSelectOnMap)
        }

        view?.setItems(initialItems)

        fetchAddressSubject
              .subscribeOn(reactiveTransformer.ioScheduler())
              .doOnNext { view?.showLoading() }
              .switchMapSingle { newText ->
                  addressService.getSuggestionsResult(newText, commonView?.getCurrentScope()!!).mapData { newText to it }
              }
              .mapData { (newText, list) ->
                  newText to list.map { prediction ->
                      SearchItemModel.SearchItemModelPrediction(
                            location = prediction
                      )
                  }
              }
              .doOnData { (newText, list) ->
                  view?.hideLoading()

                  val newList: MutableList<SearchItemModel> = list.toMutableList()

                  if (view?.getLaunchModelArgument()?.showSelectOnMapRow == true) {
                      newList.add(0, SearchItemModel.SearchItemModelSelectOnMap)
                  }

                  if (view?.getLaunchModelArgument()?.showCurrentLocationRow == true) {
                      newList.add(0, SearchItemModel.SearchItemModelCurrentLocation)
                  }



                  view?.setItems(newList)
                  if (newText.isNotBlank() && newList.isEmpty()) {
                      view?.showEmptyView()
                  } else {
                      view?.hideEmptyView()
                  }
              }
              .subscribeSafeResponseWithShowingErrorContent()

        commonView?.actions()
              ?.subscribeSafeWithShowingErrorContent { action ->
                  Timber.d("action: $action")
                  when (action) {
                      is PresenterCommonAction.ErrorRetryClicked -> {
                          commonView?.showContent()
                      }

                      is Action.SearchTextChanged -> {
                          fetchAddressSubject.onNext(action.newText)
                      }

                      is Action.SearchItemClicked -> {
                          when (action.model) {
                              is SearchItemModel.SearchItemModelCurrentLocation -> {
                                  fetchCurrentLocation()
                              }

                              is SearchItemModel.SearchItemModelPrediction -> {
                                  commonView?.closeScreenWithResult(
                                        SearchAddressPrediction(action.model.location, false)
                                  )
                              }

                              is SearchItemModel.SearchItemModelSelectOnMap -> {
                                  goToSelectOnMap(view?.getLaunchModelArgument()?.addressTypeName)
                              }
                          }
                      }
                  }
              }

        view?.getLaunchModelArgument()?.defaultLocation?.let {
            //If we receive location from outside then set it as default address
            view?.setDefaultAddress(it.address)
        }
    }

    private fun goToSelectOnMap(addressTypename: String?) {
        heartNavigator.getLauncher(OpenSelectAddressOnMap(addressTypename))
              ?.startActivityForResult()
              ?.doOnSuccess {
                  commonView?.closeScreenWithResult(it)
              }?.subscribeSafeWithShowingErrorContent()
    }

    private fun fetchCurrentLocation() {
        currentLocation.getCurrentLocation(commonView?.getCurrentScope()!!)
              .subscribeOn(reactiveTransformer.ioScheduler())
              .doOnData { prediction ->
                  Timber.d("fetchCurrentLocation received the value:$prediction")
                  commonView?.closeScreenWithResult(
                        SearchAddressPrediction(prediction, true)
                  )
              }
              .subscribeSafeResponseWithShowingErrorContent()
    }

    interface View : PresenterView {
        fun getLaunchModelArgument(): SearchAddressLaunchModel?

        fun showLoading()
        fun hideLoading()
        fun showEmptyView()
        fun hideEmptyView()
        fun setItems(items: List<SearchItemModel>)
        fun setDefaultAddress(defaultAddress: String)
    }

    sealed class Action : PresenterAction {
        data class SearchTextChanged(val newText: String) : Action()
        data class SearchItemClicked(val model: SearchItemModel) : Action()
    }
}
