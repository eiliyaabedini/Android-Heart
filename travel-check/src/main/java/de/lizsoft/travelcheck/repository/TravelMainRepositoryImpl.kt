package de.lizsoft.travelcheck.repository

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel
import de.lizsoft.heart.interfaces.common.LocalStorageManager
import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.model.common.CityTravelModel
import de.lizsoft.travelcheck.model.common.HotelOfferTravelModel
import de.lizsoft.travelcheck.model.common.HotelOfferValidationTravelModel
import de.lizsoft.travelcheck.model.common.PriceTravelModel
import de.lizsoft.travelcheck.usecase.TravelCheckFilterManager
import de.lizsoft.travelcheck.usecase.TravelCheckSortManager
import io.reactivex.Single

class TravelMainRepositoryImpl(
      private val lastMinutesTravelRepository: LastMinutesTravelRepository,
      private val travelCheckFilterManager: TravelCheckFilterManager,
      private val travelCheckSortManager: TravelCheckSortManager,
      private val settingsProvider: SettingsProvider,
      private val localStorageManager: LocalStorageManager
) : TravelMainRepository {

    private val repositories: MutableList<Pair<RepositoryType, TravelRepository>> = mutableListOf(
          RepositoryType.LASTMINUTE to lastMinutesTravelRepository
    )

    override fun getAllDestinations(forceUpdate: Boolean, showOnlyFavourites: Boolean, combineWithFlexible: Boolean): Single<List<ViewModel>> {
        return repositories[0].second
              .fetchDestinations(forceUpdate)
              .map { lastMinuteResponse -> travelCheckFilterManager.filterAllDestinations(lastMinuteResponse) }
              .map { models ->
                  if (showOnlyFavourites) {
                      models.filterIsInstance<CityTravelModel>()
                            .filter { city -> settingsProvider.getFavouriteCities().contains(city.id) }
                  } else {
                      models
                  }
              }
              .flatMap { normalResult ->
                  if (combineWithFlexible) {
                      repositories[0].second
                            .fetchFlexibleDestinations()
                            .map { lastMinuteResponse -> travelCheckFilterManager.filterAllDestinations(lastMinuteResponse) }
                            .map { it.filterIsInstance<CityTravelModel>() }
                            .map { flexibleResponse ->
                                normalResult.map { viewModel ->
                                    if (viewModel is CityTravelModel) {
                                        val flexibleCity: CityTravelModel? = flexibleResponse.firstOrNull { it.id == viewModel.id }
                                        if (flexibleCity != null && viewModel.price.amount > flexibleCity.price.amount) {
                                            flexibleCity
                                        } else {
                                            viewModel
                                        }
                                    } else {
                                        viewModel
                                    }
                                }
                            }
                  } else {
                      Single.just(normalResult)
                  }
              }
              .map { items -> travelCheckSortManager.sort(items) }
              .doOnSuccess { items ->
                  saveData(items)
              }
    }

    private fun saveData(items: List<ViewModel>) {
        items.filterIsInstance(CityTravelModel::class.java)
              .forEach { city ->
                  localStorageManager.pushToHistoricalListByKey(
                        key = city.getUniqueKey(),
                        model = city.price,
                        clazz = PriceTravelModel::class.java,
                        pushIfChanged = true,
                        recordsLimitation = 2
                  )
              }
    }

    override fun getHotelOffers(repositoryType: RepositoryType, cityId: Int, hotelId: Int, pageSize: Int): Single<List<HotelOfferTravelModel>> {
        return when (repositoryType) {
            RepositoryType.LASTMINUTE -> lastMinutesTravelRepository.getHotelOffers(
                  cityId, hotelId, pageSize
            )
        }
    }

    override fun getHotelOfferValidation(repositoryType: RepositoryType, offerId: String, cityId: Int, hotelId: Int): Single<HotelOfferValidationTravelModel> {
        return when (repositoryType) {
            RepositoryType.LASTMINUTE -> lastMinutesTravelRepository.getHotelOfferValidation(
                  offerId, cityId, hotelId
            )
        }
    }

    override fun getCityOfferUrl(repositoryType: RepositoryType, cityId: String): String {
        return when (repositoryType) {
            RepositoryType.LASTMINUTE -> lastMinutesTravelRepository.getCityOfferUrl(cityId, settingsProvider.getFlexibilityFeatureEnable())
        }
    }
}