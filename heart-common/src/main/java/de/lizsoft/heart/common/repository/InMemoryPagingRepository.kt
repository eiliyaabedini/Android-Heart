package de.lizsoft.heart.common.repository

import androidx.annotation.VisibleForTesting
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

abstract class InMemoryPagingRepository<P, T> {

    private val items: MutableMap<P, MutableList<T>> = mutableMapOf()
    private var subject: BehaviorSubject<Unit> = BehaviorSubject.create()

    fun observe(): Observable<Unit> = subject

    fun getAllItemsForPage(pageNumber: P): List<T>? = items[pageNumber]?.toMutableList()

    fun getAllItems(): List<T> = items.values.flatten()

    fun setPageItems(newItems: List<T>, pageNumber: P) {
        items[pageNumber] = newItems.toMutableList()
        subject.onNext(Unit)
    }

    fun addItemsToPage(newItems: List<T>, pageNumber: P) {
        if (items[pageNumber] == null) {
            items[pageNumber] = mutableListOf()
        }

        items[pageNumber]!!.addAll(newItems.toMutableList())
        subject.onNext(Unit)
    }

    @VisibleForTesting
    fun removeItemsFromPage(pageNumber: P, removeItems: List<T>) {
        if (items[pageNumber] == null) return

        items[pageNumber]!!.removeAll(removeItems)
        subject.onNext(Unit)
    }

    protected fun removeItemFromPage(pageNumber: P, removeItem: T) {
        if (items[pageNumber] == null) return

        items[pageNumber]!!.remove(removeItem)
        subject.onNext(Unit)
    }

    protected fun removeItemFromPageByIndex(pageNumber: P, index: Int) {
        if (items[pageNumber] == null) return

        items[pageNumber]!!.removeAt(index)
        subject.onNext(Unit)
    }

    fun purgeCache() {
        items.clear()
        subject.onNext(Unit)
    }
}
