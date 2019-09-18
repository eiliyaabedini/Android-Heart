package de.lizsoft.heart.interfaces.common.rx

import de.lizsoft.heart.interfaces.ResponseResult
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

fun <T, R> Observable<ResponseResult<T>>.mapData(body: (T) -> R): Observable<ResponseResult<R>> {
    return this.map {
        when (it) {
            is ResponseResult.Success -> ResponseResult.Success(body(it.data))
            is ResponseResult.Failure -> ResponseResult.Failure<R>(it.error)
        }
    }
}

fun <T, R> Maybe<ResponseResult<T>>.mapData(body: (T) -> R): Maybe<ResponseResult<R>> {
    return this.map {
        when (it) {
            is ResponseResult.Success -> ResponseResult.Success(body(it.data))
            is ResponseResult.Failure -> ResponseResult.Failure<R>(it.error)
        }
    }
}

fun <T, R> Single<ResponseResult<T>>.mapData(body: (T) -> R): Single<ResponseResult<R>> {
    return this.map {
        when (it) {
            is ResponseResult.Success -> ResponseResult.Success(body(it.data))
            is ResponseResult.Failure -> ResponseResult.Failure<R>(it.error)
        }
    }
}

fun <T, R> Observable<ResponseResult<T>>.flatMapData(body: (T) -> Observable<ResponseResult<R>>): Observable<ResponseResult<R>> {
    return this.flatMap {
        when (it) {
            is ResponseResult.Success -> body(it.data)
            is ResponseResult.Failure -> Observable.fromCallable { ResponseResult.Failure<R>(it.error) }
        }
    }
}

fun <T, R> Single<ResponseResult<T>>.flatMapData(body: (T) -> Single<ResponseResult<R>>): Single<ResponseResult<R>> {
    return this.flatMap {
        when (it) {
            is ResponseResult.Success -> body(it.data)
            is ResponseResult.Failure -> Single.fromCallable { ResponseResult.Failure<R>(it.error) }
        }
    }
}

fun <T, R> Maybe<ResponseResult<T>>.flatMapData(body: (T) -> Maybe<ResponseResult<R>>): Maybe<ResponseResult<R>> {
    return this.flatMap {
        when (it) {
            is ResponseResult.Success -> body(it.data)
            is ResponseResult.Failure -> Maybe.fromCallable { ResponseResult.Failure<R>(it.error) }
        }
    }
}

fun <T, R> Maybe<ResponseResult<T>>.flatMapObservableData(body: (T) -> Observable<ResponseResult<R>>): Observable<ResponseResult<R>> {
    return this.flatMapObservable {
        when (it) {
            is ResponseResult.Success -> body(it.data)
            is ResponseResult.Failure -> Observable.fromCallable { ResponseResult.Failure<R>(it.error) }
        }
    }
}

fun <T, R> Maybe<ResponseResult<T>>.flatMapSingleData(body: (T) -> Single<ResponseResult<R>>): Single<ResponseResult<R>> {
    return this.flatMapSingle {
        when (it) {
            is ResponseResult.Success -> body(it.data)
            is ResponseResult.Failure -> Single.fromCallable { ResponseResult.Failure<R>(it.error) }
        }
    }
}

fun <T, R> Maybe<ResponseResult<T>>.flatMapMaybeData(body: (T) -> Maybe<ResponseResult<R>>): Maybe<ResponseResult<R>> {
    return this.flatMap {
        when (it) {
            is ResponseResult.Success -> body(it.data)
            is ResponseResult.Failure -> Maybe.fromCallable { ResponseResult.Failure<R>(it.error) }
        }
    }
}

fun <U, V> Observable<ResponseResult<U>>.mapDataSafely(transformer: (U) -> V): Observable<ResponseResult<V>> {
    return mapData(transformer).onErrorReturn { ResponseResult.Failure(it) }
}

fun <T> Observable<ResponseResult<T>>.doOnData(data: (T) -> Unit): Observable<ResponseResult<T>> {
    return doOnNext { result ->
        when (result) {
            is ResponseResult.Success -> data(result.data)
        }
    }
}

fun <T> Single<ResponseResult<T>>.doOnData(data: (T) -> Unit): Single<ResponseResult<T>> {
    return doOnSuccess { result ->
        when (result) {
            is ResponseResult.Success -> data(result.data)
        }
    }
}

fun <T> Maybe<ResponseResult<T>>.doOnData(data: (T) -> Unit): Maybe<ResponseResult<T>> {
    return doOnSuccess { result ->
        when (result) {
            is ResponseResult.Success -> data(result.data)
        }
    }
}

fun <T> Observable<ResponseResult<T>>.doOnFailure(handler: (Throwable) -> Unit): Observable<ResponseResult<T>> {
    return doOnNext { result ->
        when (result) {
            is ResponseResult.Failure -> handler(result.error)
        }
    }
}

fun <T> Single<ResponseResult<T>>.doOnFailure(handler: (Throwable) -> Unit): Single<ResponseResult<T>> {
    return doOnSuccess { result ->
        when (result) {
            is ResponseResult.Failure -> handler(result.error)
        }
    }
}

fun <T> Maybe<ResponseResult<T>>.doOnFailure(handler: (Throwable) -> Unit): Maybe<ResponseResult<T>> {
    return doOnSuccess { result ->
        when (result) {
            is ResponseResult.Failure -> handler(result.error)
        }
    }
}

fun <T> Observable<ResponseResult<T>>.toListData(): Single<ResponseResult<List<T>>> {
    return this.toList()
          .map { list ->
              val success: List<ResponseResult.Success<*>> = list.filterIsInstance(ResponseResult.Success::class.java)
              val failures = list.filterIsInstance(ResponseResult.Failure::class.java)

              if (failures.isNotEmpty()) return@map ResponseResult.Failure<List<T>>(failures.first().error)

              ResponseResult.Success(success.map { it.data as T })
          }
}

fun <T> Observable<T>.toResult(): Observable<ResponseResult<T>> {
    return map { data ->
        ResponseResult.Success(data) as ResponseResult<T>
    }.onErrorReturn { throwable ->
        ResponseResult.Failure(throwable)
    }
}

fun <T> Single<T>.toResult(): Single<ResponseResult<T>> {
    return map { data ->
        ResponseResult.Success(data) as ResponseResult<T>
    }.onErrorReturn { throwable ->
        ResponseResult.Failure(throwable)
    }
}

fun <T> Maybe<T>.toResult(): Maybe<ResponseResult<T>> {
    return map { data ->
        ResponseResult.Success(data) as ResponseResult<T>
    }.onErrorReturn { throwable ->
        ResponseResult.Failure(throwable)
    }
}

fun <T> Observable<ResponseResult<T>>.toNotResult(): Observable<T> {
    return flatMap { response ->
        when (response) {
            is ResponseResult.Success -> Observable.fromCallable { response.data }
            is ResponseResult.Failure -> Observable.error(response.error)
        }
    }
}

fun <T> Single<ResponseResult<T>>.toNotResult(): Single<T> {
    return flatMap { response ->
        when (response) {
            is ResponseResult.Success -> Single.fromCallable { response.data }
            is ResponseResult.Failure -> Single.error(response.error)
        }
    }
}

