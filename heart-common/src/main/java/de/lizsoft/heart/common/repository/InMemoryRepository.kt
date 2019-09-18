package de.lizsoft.heart.common.repository

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

abstract class InMemoryRepository<K, T> {

    private val items: MutableMap<K, T> = mutableMapOf()
    private var subject: BehaviorSubject<Unit> = BehaviorSubject.create()

    fun observe(): Observable<Unit> = subject

    fun getAllItems(): List<T> = items.values.toList()

    fun getItemsByKey(key: K) : T? = items[key]

    fun setItems(newItems: Map<K, T>) {
        items.clear()

        addItems(newItems)
    }

    fun addItems(newItems: Map<K, T>) {
        items.putAll(newItems)

        subject.onNext(Unit)
    }

    fun addItem(key: K, value: T) {
        items.put(key, value)

        subject.onNext(Unit)
    }

    fun removeItemByKey(key: K) {
        items.remove(key)

        subject.onNext(Unit)
    }

    fun purgeCache() {
        items.clear()

        subject.onNext(Unit)
    }
}