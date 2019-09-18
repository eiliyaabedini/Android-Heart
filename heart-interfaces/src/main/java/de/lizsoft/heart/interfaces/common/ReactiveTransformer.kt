package de.lizsoft.heart.interfaces.common

import io.reactivex.CompletableTransformer
import io.reactivex.MaybeTransformer
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers

/**
 * Provides observable transformers that simplify the usage of [Observable.subscribeOn]
 * and [Observable.observeOn] methods.
 */
interface ReactiveTransformer {

    /**
     * Simplification for the subscribeOn and observeOn methods, subscribing on [Schedulers.io].
     * See [.transformer].
     */
    fun <T> ioTransformer(): ObservableTransformer<T, T>

    /**
     * Simplification for the subscribeOn and observeOn methods, subscribing on [Schedulers.trampoline].
     * See [.transformer].
     */
    fun <T> immediateTransformer(): ObservableTransformer<T, T>

    /**
     * Simplification for the subscribeOn and observeOn methods, subscribing on [Schedulers.computation].
     * See [.transformer].
     */
    fun <T> computationTransformer(): ObservableTransformer<T, T>

    /**
     * @see .ioTransformer
     */
    fun <T> ioSingleTransformer(): SingleTransformer<T, T>

    /**
     * @see .ioTransformer
     */
    fun <T> ioMaybeTransformer(): MaybeTransformer<T, T>

    /**
     * @see .computationTransformer
     */
    fun <T> computationSingleTransformer(): SingleTransformer<T, T>

    /**
     * @see .ioTransformer
     */
    fun ioCompletableTransformer(): CompletableTransformer

    /**
     * @see .computationTransformer
     */
    fun computationCompletableTransformer(): CompletableTransformer

    /**
     * @see .computationTransformer
     */
    fun <T> computationMaybeTransformer(): MaybeTransformer<T, T>

    fun <T> singleThreadSingleTransformer(): SingleTransformer<T, T>

    /**
     * Simplification for the subscribeOn and observeOn method. See the example below:
     *
     * <pre>`
     * Observable<NewsFeed> streamCachedFeed = streamCachedFeed();
     * streamCachedFeed.subscribeOn(Schedulers.io())
     * .observeOn(AndroidSchedulers.mainThread())
     * .subscribe();
     *
     * becomes ->
     *
     * Observable<NewsFeed> streamCachedFeed = streamCachedFeed();
     * streamCachedFeed.compose(transformers.transformer(backgroundScheduler))
     * .subscribe();
    </NewsFeed></NewsFeed>`</pre> *
     */
    fun <T> transformer(scheduler: Scheduler): ObservableTransformer<T, T>

    /**
     * @see .transformer
     */
    fun <T> singleTransformer(scheduler: Scheduler): SingleTransformer<T, T>

    /**
     * @see .transformer
     */
    fun <T> maybeTransformer(scheduler: Scheduler): MaybeTransformer<T, T>

    /**
     * @see .transformer
     */
    fun completableTransformer(scheduler: Scheduler): CompletableTransformer

    fun ioScheduler(): Scheduler

    fun immediateScheduler(): Scheduler

    fun computationScheduler(): Scheduler

    fun mainThreadScheduler(): Scheduler
}
