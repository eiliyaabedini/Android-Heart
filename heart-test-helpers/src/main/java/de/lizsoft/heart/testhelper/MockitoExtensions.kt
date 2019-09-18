package de.lizsoft.heart.testhelper

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

fun <T> T.spy() = Mockito.spy(this)

@JvmName("thenErrorObservable")
fun <T> OngoingStubbing<Observable<T>>.thenError(throwable: Throwable) {
    thenReturn(Observable.error(throwable))
}

fun OngoingStubbing<Completable>.thenComplete() {
    thenReturn(Completable.complete())
}

@JvmName("thenErrorCompletable")
fun OngoingStubbing<Completable>.thenError(throwable: Throwable) {
    thenReturn(Completable.error(throwable))
}

fun <T> OngoingStubbing<Maybe<T>>.thenEmpty() {
    thenReturn(Maybe.empty<T>())
}

@JvmName("thenJustMaybe")
fun <T> OngoingStubbing<Maybe<T>>.thenJust(value: T): OngoingStubbing<Maybe<T>> {
    return thenReturn(Maybe.just(value))
}

@JvmName("thenErrorMaybe")
fun <T> OngoingStubbing<Maybe<T>>.thenError(throwable: Throwable) {
    thenReturn(Maybe.error(throwable))
}

@JvmName("thenJustSingle")
fun <T> OngoingStubbing<Single<T>>.thenJust(value: T) {
    thenReturn(Single.just(value))
}

@JvmName("thenJustObservable")
fun <T> OngoingStubbing<Observable<T>>.thenJust(value: T) {
    thenReturn(Observable.just(value))
}

@JvmName("thenErrorSingle")
fun <T> OngoingStubbing<Single<T>>.thenError(throwable: Throwable) {
    thenReturn(Single.error(throwable))
}
