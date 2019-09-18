package de.lizsoft.heart.common.repository

import com.nhaarman.mockitokotlin2.mock
import de.lizsoft.heart.interfaces.ResponseResult
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InMemoryApiRepositoryPagingTest {

    private lateinit var repository: InMemoryTestApiPagingRepository

    @Before
    fun setUp() {
        repository = InMemoryTestApiPagingRepository()
    }

    @Test
    fun `When fetching call getData`() {
        repository.fetch()

        assertEquals(repository.calledGetData, 1)
    }

    @Test
    fun `When new data received then inform observers`() {
        repository.fetch()

        repository.observe().test()
              .assertValueCount(1)
              .assertNotComplete()
              .assertNoErrors()
    }

    @Test
    fun `When failure received then do not inform observers and do not change caches`() {
        repository.returnType = 1
        repository.fetch()

        repository.observe().test()
              .assertValueCount(0)
              .assertNotComplete()
              .assertNoErrors()
    }

    @Test
    fun `When error received then do not inform observers and do not change caches`() {
        repository.returnType = 2
        repository.fetch()

        repository.observe().test()
              .assertValueCount(0)
              .assertNotComplete()
              .assertNoErrors()
    }

    data class RepositoryTestModel(val id: String, val name: String)

    class InMemoryTestApiPagingRepository : InMemoryApiPagingRepository<RepositoryTestModel>() {
        var returnType: Int = 0 //0 -> Success, 1-> Failure, 2-> Error
        var calledGetData: Int = 0

        override fun getDataByPageNumber(pageNumber: Int): Single<ResponseResult<List<RepositoryTestModel>>> {
            calledGetData++
            return when (returnType) {
                0 -> {
                    Single.just(
                          ResponseResult.Success(
                                listOf(
                                      RepositoryTestModel("$calledGetData-1", "$calledGetData-1-name"),
                                      RepositoryTestModel("$calledGetData-2", "$calledGetData-2-name"),
                                      RepositoryTestModel("$calledGetData-3", "$calledGetData-3-name")
                                )
                          )
                    )
                }

                1 -> {
                    Single.just(
                          ResponseResult.Failure(mock())
                    )
                }

                else -> Single.error(mock<Exception>())
            }
        }
    }
}