package de.lizsoft.heart.common.event

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class EventManager {

    private val subject: PublishSubject<DataEvent> = PublishSubject.create()

    fun observe(): Observable<DataEvent> = subject

    fun notify(event: DataEvent) {
        if (enabled) subject.onNext(event)
    }

    var enabled = true
}

