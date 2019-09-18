package de.lizsoft.heart.interfaces.common.rx

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.Observables
import java.util.concurrent.TimeUnit

fun <T> Single<T>.zipWithDelay(delay: Long = 2000): Single<T> {
    return zipWith(Single.timer(delay, TimeUnit.MILLISECONDS), BiFunction { item: T, _: Long -> item })
}

fun <T> Maybe<T>.zipWithDelay(delay: Long = 2000): Maybe<T> {
    return zipWith(Maybe.timer(delay, TimeUnit.MILLISECONDS), BiFunction { item: T, _: Long -> item })
}

fun <T> Observable<T>.zipWithDelay(delay: Long = 2000): Observable<T> {
    return zipWith(Observable.timer(delay, TimeUnit.MILLISECONDS), BiFunction { item: T, _: Long -> item })
}

fun <T, R> Observable<T>.mapNotNull(body: (T) -> R?): Observable<R> {
    return filter { body(it) != null }.map { body(it)!! }
}

fun <T, R> Maybe<T>.mapNotNull(body: (T) -> R?): Maybe<R> {
    return filter { body(it) != null }.map { body(it)!! }
}

/**
 * Combines two source ObservableSources by emitting an item that aggregates the latest values of each of the
 * source Observable each time an item is received from either of the source Observable. Combines the two
 * items into a Triple along with an index of which source triggered the latest emission.
 */
fun <T1, T2> Observables.combineLatestWithIndex(
      source1: Observable<T1>,
      source2: Observable<T2>
): Observable<Triple<T1, T2, Int>> {
    return combineLatest(source1.timestamp(), source2.timestamp())
          .map { (first, second) ->
              Triple(first.value(), second.value(), if (first.time() > second.time()) 0 else 1)
          }
}

fun <T1, T2, T3, R> Observables.combineLatestWithIndex(
      source1: Observable<T1>,
      source2: Observable<T2>,
      source3: Observable<T3>,
      combineFunction: (T1, T2, T3, Int) -> R
): Observable<R> {
    return combineLatest(source1.timestamp(), source2.timestamp(), source3.timestamp())
          .map { (first, second, third) ->
              combineFunction(
                    first.value(),
                    second.value(),
                    third.value(),
                    arrayOf(first.time(), second.time(), third.time()).indexOfMax()
              )
          }
}

fun <T : Comparable<T>> Array<out T>.indexOfMax(): Int {
    return if (isEmpty()) -1 else indexOf(this.max())
}

fun <T1, T2, T3> Observables.combineLatestWithIndex(
      source1: Observable<T1>,
      source2: Observable<T2>,
      source3: Observable<T3>
): Observable<Tuple4<T1, T2, T3, Int>> {
    return combineLatest(source1.timestamp(), source2.timestamp(), source3.timestamp())
          .map { (first, second, third) ->
              Tuple4(
                    first.value(),
                    second.value(),
                    third.value(),
                    arrayOf(first.time(), second.time(), third.time()).indexOfMax()
              )
          }
}

data class Tuple4<out A, out B, out C, out D>(
      val first: A,
      val second: B,
      val third: C,
      val fourth: D
) {

    override fun toString(): String = "($first, $second, $third, $fourth)"
}
