package de.lizsoft.travelcheck.db

import de.lizsoft.heart.common.extension.addDays
import de.lizsoft.heart.common.extension.getDateFormatUTC
import de.lizsoft.heart.common.extension.toDateWithFormat
import de.lizsoft.heart.interfaces.common.LocalStorageManager
import de.lizsoft.travelcheck.model.common.Airport
import de.lizsoft.travelcheck.model.common.SortType
import java.util.*

class SettingsProvider(
      private val localStorageManager: LocalStorageManager
) {

    fun getStartDate(): String {
        val startDate: String = localStorageManager.getStringByKey(START_DATE_KEY, null)
              ?: return Date()
                    .addDays(3)
                    .formatString()

        val startDateDt = startDate.toDateWithFormat(DATE_FORMAT)
        if (startDateDt.before(Date().addDays(1))) {
            return Date()
                  .addDays(3)
                  .formatString()
        }
        return startDate
    }

    fun setStartDate(date: Date) {
        localStorageManager.saveStringByKey(START_DATE_KEY, date.formatString())
    }

    fun getEndDate(): String {
        val endDate: String = localStorageManager.getStringByKey(END_DATE_KEY, null)
              ?: return Date()
                    .addDays(45)
                    .formatString()

        val endDateDt = endDate.toDateWithFormat(DATE_FORMAT)
        if (endDateDt.before(Date().addDays(10))) {
            return Date()
                  .addDays(45)
                  .formatString()
        }
        return endDate
    }

    fun setEndDate(date: Date) {
        return localStorageManager.saveStringByKey(END_DATE_KEY, date.formatString())
    }

    fun getStars(): String {
        return localStorageManager.getStringByKey(STARS_KEY, "4")!!
    }

    fun getDepartureAirport(): Airport {
        return Airport.values().first { it.code == localStorageManager.getStringByKey(DEPARTURE_AIRPORT_CODE_KEY, "HAM")!! }
    }

    fun setDepartureAirport(airport: Airport) {
        localStorageManager.saveStringByKey(DEPARTURE_AIRPORT_CODE_KEY, airport.code)
    }

    fun getDepartureFlexibleAirports(): List<Airport> {
        return localStorageManager.getListByKey(DEPARTURE_FLEXIBLE_AIRPORT_CODE_KEY, Airport::class.java)
              ?: emptyList()
    }

    fun setDepartureFlexibleAirports(airports: List<Airport>) {
        localStorageManager.setListsByKey(DEPARTURE_FLEXIBLE_AIRPORT_CODE_KEY, airports, Airport::class.java)
    }

    fun getNumberOfAdults(): Int {
        return localStorageManager.getIntByKey(NUMBER_OF_ADULTS_KEY, 1)
    }

    fun setNumberOfAdults(adultsNr: Int) {
        localStorageManager.saveIntByKey(NUMBER_OF_ADULTS_KEY, adultsNr)
    }

    fun getSortType(): SortType {
        return SortType.values()[localStorageManager.getIntByKey(SORT_TYPE_KEY, SortType.NONE.ordinal)]
    }

    fun setSortType(type: SortType) {
        localStorageManager.saveIntByKey(SORT_TYPE_KEY, type.ordinal)
    }

    fun getChildAge(): Int {
        return localStorageManager.getIntByKey(CHILD_AGE_KEY, 2)
    }

    fun setChildAge(childAge: Int) {
        localStorageManager.saveIntByKey(CHILD_AGE_KEY, childAge)
    }

    fun getDuration(): Int {
        return localStorageManager.getIntByKey(DURATION_KEY, 7)
    }

    fun setDuration(duration: Int) {
        localStorageManager.saveIntByKey(DURATION_KEY, duration)
    }

    fun getRoutineMaxPrice(): Int {
        return localStorageManager.getIntByKey(ROUTINE_MAX_PRICE_KEY, 250)
    }

    fun setRoutineMaxPrice(maxPrice: Int) {
        localStorageManager.saveIntByKey(ROUTINE_MAX_PRICE_KEY, maxPrice)
    }

    fun getFavouriteCities(): List<Int> {
        return localStorageManager.getListByKey(FAVOURITE_CITIES_KEY, Int::class.java)
              ?: emptyList()
    }

    fun addFavouriteCity(cityId: Int) {
        return localStorageManager.pushToListByKey(FAVOURITE_CITIES_KEY, cityId, Int::class.java)
    }

    fun removeFavouriteCity(cityId: Int) {
        return localStorageManager.removeFromListByKey(FAVOURITE_CITIES_KEY, cityId, Int::class.java)
    }

    fun getFlexibilityFeatureEnable(): Boolean {
        return localStorageManager.getBoolean(FLEXIBILITY_IS_ENABLED_KEY, false)
    }

    fun setFlexibilityFeatureEnable(isFlexible: Boolean) {
        localStorageManager.setBoolean(FLEXIBILITY_IS_ENABLED_KEY, isFlexible)
    }

    private fun Date.formatString(): String = getDateFormatUTC(DATE_FORMAT).format(this)

    companion object {
        private const val START_DATE_KEY = "TravelStartDateKey"
        private const val END_DATE_KEY = "TravelEndDateKey"
        private const val STARS_KEY = "TravelStarsKey"
        private const val SORT_TYPE_KEY = "TravelSortTypeKey"
        private const val ROUTINE_MAX_PRICE_KEY = "TravelRoutineMaxPriceKey"
        private const val DEPARTURE_AIRPORT_CODE_KEY = "TravelDepartureAirportCodeKey"
        private const val DEPARTURE_FLEXIBLE_AIRPORT_CODE_KEY = "TravelDepartureFlexibleAirportCodeKey"
        private const val NUMBER_OF_ADULTS_KEY = "TravelNumberOfAdultsKey"
        private const val CHILD_AGE_KEY = "TravelChildAgeKey"
        private const val DURATION_KEY = "TravelDurationKey"
        private const val FAVOURITE_CITIES_KEY = "TravelFavouriteCitiesKey"
        private const val FLEXIBILITY_IS_ENABLED_KEY = "TravelFlexibilityIsEnabledKey"

        const val DATE_FORMAT = "yyyy-MM-dd"
    }
}