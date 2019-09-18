package de.lizsoft.travelcheck.usecase

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel

interface TravelCheckSortManager {

    fun <T : ViewModel> sort(list: List<T>): List<ViewModel>
}