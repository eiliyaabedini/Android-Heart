package de.lizsoft.heart.interfaces.common.rx

import io.reactivex.Observable

data class SingleRepositoryResult<out T>(
      val data: T?,
      val state: SingleRepositoryState,
      val fromCache: Boolean?
) {

    fun <U> map(transform: (T) -> U): SingleRepositoryResult<U> {
        return SingleRepositoryResult(data?.let(transform), state, fromCache)
    }

    fun mapError(transform: (Throwable) -> Throwable): SingleRepositoryResult<T> {
        return when (state) {
            is SingleRepositoryState.Error -> {
                copy(state = SingleRepositoryState.Error(transform(state.throwable)))
            }

            else -> this
        }
    }
}

sealed class SingleRepositoryState {
    object Idle : SingleRepositoryState()
    object Loading : SingleRepositoryState()
    data class Error(val throwable: Throwable) : SingleRepositoryState()
}

fun <T, R> Observable<SingleRepositoryResult<T>>.mapData(transform: (T) -> R): Observable<SingleRepositoryResult<R>> {
    return map { it.map(transform) }
}

fun <T> Observable<SingleRepositoryResult<T>>.mapError(transform: (Throwable) -> Throwable): Observable<SingleRepositoryResult<T>> {
    return map { it.mapError(transform) }
}

fun <T> Observable<SingleRepositoryResult<T>>.doOnData(action: (data: T, fromCache: Boolean) -> Unit): Observable<SingleRepositoryResult<T>> {
    return doOnNext { result ->
        when (result.state) {
            is SingleRepositoryState.Idle -> {
                if (result.data != null && result.fromCache != null) {
                    action(result.data, result.fromCache)
                }
            }
        }
    }
}
