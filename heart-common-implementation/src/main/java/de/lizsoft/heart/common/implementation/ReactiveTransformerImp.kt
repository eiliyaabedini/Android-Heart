package de.lizsoft.heart.common.implementation

import io.reactivex.CompletableTransformer
import io.reactivex.MaybeTransformer
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers
import de.lizsoft.heart.interfaces.common.ReactiveTransformer

/**
 * Provides observable transformers that simplify the usage of [Observable.subscribeOn]
 * and [Observable.observeOn] methods.
 */
open class ReactiveTransformerImp(
    private val io: Scheduler,
    private val trampoline: Scheduler,
    private val computation: Scheduler,
    private val single: Scheduler,
    private val mainThread: Scheduler
) : ReactiveTransformer {

    /**
     * Simplification for the subscribeOn and observeOn methods, subscribing on [Schedulers.io].
     * See [.transformer].
     */
    override fun <T> ioTransformer(): ObservableTransformer<T, T> {
        return transformer(io)
    }

    /**
     * Simplification for the subscribeOn and observeOn methods, subscribing on [Schedulers.trampoline].
     * See [.transformer].
     */
    override fun <T> immediateTransformer(): ObservableTransformer<T, T> {
        return transformer(trampoline)
    }

    /**
     * Simplification for the subscribeOn and observeOn methods, subscribing on [Schedulers.computation].
     * See [.transformer].
     */
    override fun <T> computationTransformer(): ObservableTransformer<T, T> {
        return transformer(computation)
    }

    /**
     * @see .ioTransformer
     */
    override fun <T> ioSingleTransformer(): SingleTransformer<T, T> {
        return singleTransformer(io)
    }

    /**
     * @see .ioTransformer
     */
    override fun <T> ioMaybeTransformer(): MaybeTransformer<T, T> {
        return maybeTransformer(io)
    }

    /**
     * @see .computationTransformer
     */
    override fun <T> computationSingleTransformer(): SingleTransformer<T, T> {
        return singleTransformer(computation)
    }

    /**
     * @see .ioTransformer
     */
    override fun ioCompletableTransformer(): CompletableTransformer {
        return completableTransformer(io)
    }

    /**
     * @see .computationTransformer
     */
    override fun computationCompletableTransformer(): CompletableTransformer {
        return completableTransformer(computation)
    }

    /**
     * @see .computationTransformer
     */
    override fun <T> computationMaybeTransformer(): MaybeTransformer<T, T> {
        return maybeTransformer(computation)
    }

    override fun <T> singleThreadSingleTransformer(): SingleTransformer<T, T> {
        return singleTransformer(single)
    }

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
    override fun <T> transformer(scheduler: Scheduler): ObservableTransformer<T, T> {
        return ObservableTransformer { observable -> observable.subscribeOn(scheduler).observeOn(mainThread) }
    }

    /**
     * @see .transformer
     */
    override fun <T> singleTransformer(scheduler: Scheduler): SingleTransformer<T, T> {
        return SingleTransformer { single -> single.subscribeOn(scheduler).observeOn(mainThread) }
    }

    /**
     * @see .transformer
     */
    override fun <T> maybeTransformer(scheduler: Scheduler): MaybeTransformer<T, T> {
        return MaybeTransformer { maybe -> maybe.subscribeOn(scheduler).observeOn(mainThread) }
    }

    /**
     * @see .transformer
     */
    override fun completableTransformer(scheduler: Scheduler): CompletableTransformer {
        return CompletableTransformer { completable -> completable.subscribeOn(scheduler).observeOn(mainThread) }
    }

    override fun ioScheduler(): Scheduler {
        return io
    }

    override fun immediateScheduler(): Scheduler {
        return trampoline
    }

    override fun computationScheduler(): Scheduler {
        return computation
    }

    override fun mainThreadScheduler(): Scheduler {
        return mainThread
    }
}
