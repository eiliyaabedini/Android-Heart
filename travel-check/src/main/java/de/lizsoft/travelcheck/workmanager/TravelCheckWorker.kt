package de.lizsoft.travelcheck.workmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import de.lizsoft.travelcheck.R
import de.lizsoft.travelcheck.db.SettingsProvider
import de.lizsoft.travelcheck.model.common.CityTravelModel
import de.lizsoft.travelcheck.model.common.HotelOfferTravelModel
import de.lizsoft.travelcheck.model.common.HotelOfferValidationTravelModel
import de.lizsoft.travelcheck.repository.TravelMainRepository
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class TravelCheckWorker(
      private val appContext: Context,
      workerParams: WorkerParameters
) : RxWorker(appContext, workerParams), KoinComponent {

    private val settingsProvider: SettingsProvider by inject()
    private val mainTravelRepository: TravelMainRepository by inject()

    override fun createWork(): Single<Result> {

        return mainTravelRepository.getAllDestinations(forceUpdate = true, showOnlyFavourites = true)
              .map { cities ->
                  cities.filterIsInstance<CityTravelModel>()
                        .filter { city ->
                            city.price.amount <= settingsProvider.getRoutineMaxPrice()
                        }
              }
              .flattenAsObservable { it }
              .flatMapMaybe { city ->
                  mainTravelRepository.getHotelOffers(
                        repositoryType = city.repositoryType,
                        cityId = city.id,
                        hotelId = city.hotelId,
                        pageSize = 1
                  )
                        .flattenAsObservable { it }
                        .flatMapMaybe { hotelOffer ->
                            mainTravelRepository.getHotelOfferValidation(
                                  repositoryType = hotelOffer.repositoryType,
                                  offerId = hotelOffer.id,
                                  cityId = city.id,
                                  hotelId = hotelOffer.hotelId
                            )
                                  .filter { it.totalPrice.amount > 0 }
                                  .map { hotelOffer to it }
                        }
                        .firstElement()
                        .doOnSuccess { (hotelOffer, hotelOfferValidationTravelModel) ->
                            showNotification(
                                  cityTravelModel = city,
                                  hotelOfferTravelModel = hotelOffer,
                                  hotelOfferValidationTravelModel = hotelOfferValidationTravelModel
                            )
                        }
              }
              .toList()
              .map { Result.success() }
              .onErrorReturn {
                  it.printStackTrace()
                  Result.failure()
              }
    }

    private fun showNotification(
          cityTravelModel: CityTravelModel,
          hotelOfferTravelModel: HotelOfferTravelModel,
          hotelOfferValidationTravelModel: HotelOfferValidationTravelModel
    ) {
        Timber.d("showNotification called")
        val channelId = appContext.getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(appContext, channelId)
              .setSmallIcon(R.drawable.ic_card_travel_black_24dp)
              .setContentTitle("Offer found! - ${cityTravelModel.regionName}")
              .setContentText("${cityTravelModel.name}: ${hotelOfferTravelModel.price.amount} -> ${hotelOfferValidationTravelModel.totalPrice.amount}")
              .setAutoCancel(true)

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                  channelId,
                  "Default notifications",
                  NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(cityTravelModel.id.hashCode(), notificationBuilder.build())
    }
}