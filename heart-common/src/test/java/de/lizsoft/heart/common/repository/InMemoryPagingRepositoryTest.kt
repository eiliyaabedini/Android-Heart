package de.lizsoft.heart.common.repository

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InMemoryPagingRepositoryTest {

    private lateinit var repository: InMemoryTestRepository

    @Before
    fun setUp() {
        repository = InMemoryTestRepository()
    }

    @Test
    fun `When adding new page items then inform observers`() {
        repository.setPageItems(emptyList(), 0)
        repository.setPageItems(emptyList(), 1)
        repository.setPageItems(emptyList(), 2)
        repository.setPageItems(emptyList(), 1)
        repository.setPageItems(emptyList(), 2)

        repository.observe().test()
              .awaitCount(5)
              .assertNotComplete()
              .assertNoErrors()
    }

    @Test
    fun `When adding new items to a page then inform observers`() {
        repository.addItemsToPage(emptyList(), 0)
        repository.addItemsToPage(emptyList(), 0)
        repository.addItemsToPage(emptyList(), 0)
        repository.addItemsToPage(emptyList(), 0)
        repository.addItemsToPage(emptyList(), 0)

        repository.observe().test()
              .awaitCount(5)
              .assertNotComplete()
              .assertNoErrors()
    }

    @Test
    fun `When removing items from a page then inform observers`() {
        repository.removeItemsFromPage(0, emptyList())
        repository.removeItemsFromPage(0, emptyList())
        repository.removeItemsFromPage(0, emptyList())
        repository.removeItemsFromPage(1, emptyList())
        repository.removeItemsFromPage(1, emptyList())
        repository.removeItemsFromPage(0, emptyList())

        repository.observe().test()
              .awaitCount(6)
              .assertNotComplete()
              .assertNoErrors()
    }

    @Test
    fun `check getting items`() {
        repository.setPageItems(emptyList(), 0)
        repository.setPageItems(emptyList(), 1)
        repository.setPageItems(emptyList(), 2)
        repository.addItemsToPage(emptyList(), 0)
        repository.removeItemsFromPage(1, emptyList())

        assertEquals(repository.getAllItemsForPage(0), emptyList<RepositoryTestModel>())
        assertEquals(repository.getAllItemsForPage(1), emptyList<RepositoryTestModel>())
        assertEquals(repository.getAllItemsForPage(2), emptyList<RepositoryTestModel>())
    }

    @Test
    fun `check purge items`() {
        repository.setPageItems(emptyList(), 0)
        repository.setPageItems(emptyList(), 1)
        repository.setPageItems(emptyList(), 2)
        repository.addItemsToPage(emptyList(), 0)
        repository.removeItemsFromPage(1, emptyList())

        repository.purgeCache()

        assertEquals(repository.getAllItemsForPage(0), null)
        assertEquals(repository.getAllItemsForPage(1), null)
        assertEquals(repository.getAllItemsForPage(2), null)
    }

    data class RepositoryTestModel(val id: String, val name: String)

    class InMemoryTestRepository : InMemoryPagingRepository<Int, RepositoryTestModel>()
}