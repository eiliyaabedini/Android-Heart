package de.lizsoft.heart.common.repository

import androidx.annotation.CallSuper
import de.lizsoft.heart.interfaces.ResponseResult
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

abstract class InMemoryApiPagingWithUniqueIdRepository<T, U> : InMemoryPagingRepository<Int, T>() {

    private var fetchSubject: BehaviorSubject<Int> = BehaviorSubject.create()

    @CallSuper
    open fun bind(uniqueId: U) {
        fetchSubject
              .switchMapSingle { pageNumber ->
                  getDataByPageNumber(pageNumber, uniqueId).map { it to pageNumber }
              }
              .doOnNext { (result, pageNumber) ->
                  when (result) {
                      is ResponseResult.Success -> {
                          setPageItems(result.data, pageNumber)
                      }

                      is ResponseResult.Failure -> {
                          result.error.printStackTrace()
                      }
                  }
              }
              .onErrorReturn { throwable: Throwable -> ResponseResult.Failure<List<T>>(throwable) to -1 }
              .subscribe()
    }

    fun fetch(pageNumber: Int = 0) {
        fetchSubject.onNext(pageNumber)
    }

    abstract fun getDataByPageNumber(pageNumber: Int, uniqueId: U): Single<ResponseResult<List<T>>>

}
