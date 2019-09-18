package de.lizsoft.heart.common.repository

import androidx.annotation.VisibleForTesting
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

abstract class InMemoryPagingRepository<K, T> {

    private val items: MutableMap<K, MutableList<T>> = mutableMapOf()
    private var subject: BehaviorSubject<Unit> = BehaviorSubject.create()

    fun observe(): Observable<Unit> = subject

    fun getAllItemsForPage(pageNumber: K): List<T>? = items[pageNumber]?.toMutableList()

    fun getAllItems(): List<T> = items.values.flatten()

    fun setPageItems(newItems: List<T>, pageNumber: K) {
        items[pageNumber] = newItems.toMutableList()
        subject.onNext(Unit)
    }

    fun addItemsToPage(newItems: List<T>, pageNumber: K) {
        if (items[pageNumber] == null) {
            items[pageNumber] = mutableListOf()
        }

        items[pageNumber]!!.addAll(newItems.toMutableList())
        subject.onNext(Unit)
    }

    @VisibleForTesting
    fun removeItemsFromPage(pageNumber: K, removeItems: List<T>) {
        if (items[pageNumber] == null) return

        items[pageNumber]!!.removeAll(removeItems)
        subject.onNext(Unit)
    }

    protected fun removeItemFromPage(pageNumber: K, removeItem: T) {
        if (items[pageNumber] == null) return

        items[pageNumber]!!.remove(removeItem)
        subject.onNext(Unit)
    }

    protected fun removeItemFromPageByIndex(pageNumber: K, index: Int) {
        if (items[pageNumber] == null) return

        items[pageNumber]!!.removeAt(index)
        subject.onNext(Unit)
    }

    fun purgeCache() {
        items.clear()
        subject.onNext(Unit)
    }

}