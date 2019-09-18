package de.lizsoft.heart.common.repository

import com.nhaarman.mockitokotlin2.mock
import org.hamcrest.Matchers
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class InMemoryRepositoryTest {

    private val mockRepositoryTestModel1: RepositoryTestModel = mock()
    private val mockRepositoryTestModel2: RepositoryTestModel = mock()
    private val mockRepositoryTestModel3: RepositoryTestModel = mock()
    private val mockRepositoryTestModel4: RepositoryTestModel = mock()
    private val mockRepositoryTestModel5: RepositoryTestModel = mock()

    private lateinit var repository: InMemoryTestRepository

    @Before
    fun setUp() {
        repository = InMemoryTestRepository()
    }

    @Test
    fun `When setting items then inform observers`() {
        repository.setItems(emptyMap())

        repository.observe().test()
              .awaitCount(1)
              .assertNotComplete()
              .assertNoErrors()
    }

    @Test
    fun `When adding new items then inform observers`() {
        repository.addItem("1", mockRepositoryTestModel1)
        repository.addItem("2", mockRepositoryTestModel2)
        repository.addItem("3", mockRepositoryTestModel3)
        repository.addItem("1", mockRepositoryTestModel4)
        repository.addItem("1", mockRepositoryTestModel5)

        repository.observe().test()
              .awaitCount(5)
              .assertNotComplete()
              .assertNoErrors()
    }

    @Test
    fun `When adding new items in a batch then inform observers`() {
        repository.addItems(
              mapOf(
                    ("1" to mockRepositoryTestModel1),
                    ("2" to mockRepositoryTestModel2),
                    ("3" to mockRepositoryTestModel3),
                    ("4" to mockRepositoryTestModel4),
                    ("5" to mockRepositoryTestModel5)
              )
        )

        repository.observe().test()
              .awaitCount(1)
              .assertNotComplete()
              .assertNoErrors()
    }

    @Test
    fun `When removing items then inform observers`() {
        repository.removeItemByKey("0")
        repository.removeItemByKey("0")
        repository.removeItemByKey("1")
        repository.removeItemByKey("0")

        repository.observe().test()
              .awaitCount(4)
              .assertNotComplete()
              .assertNoErrors()
    }

    @Test
    fun `When adding items with batch then they should be exist when getting them`() {
        repository.addItems(
              mapOf(
                    ("1" to mockRepositoryTestModel1),
                    ("2" to mockRepositoryTestModel2),
                    ("3" to mockRepositoryTestModel3),
                    ("4" to mockRepositoryTestModel4),
                    ("5" to mockRepositoryTestModel5)
              )
        )

        assertThat(repository.getItemsByKey("1"), Matchers.equalTo(mockRepositoryTestModel1))
        assertThat(repository.getItemsByKey("2"), Matchers.equalTo(mockRepositoryTestModel2))
        assertThat(repository.getItemsByKey("3"), Matchers.equalTo(mockRepositoryTestModel3))
        assertThat(repository.getItemsByKey("4"), Matchers.equalTo(mockRepositoryTestModel4))
        assertThat(repository.getItemsByKey("5"), Matchers.equalTo(mockRepositoryTestModel5))
        assertThat(
              repository.getAllItems(), Matchers.equalTo(
              listOf(
                    mockRepositoryTestModel1,
                    mockRepositoryTestModel2,
                    mockRepositoryTestModel3,
                    mockRepositoryTestModel4,
                    mockRepositoryTestModel5
              )
        )
        )

    }

    @Test
    fun `When adding items one by one then they should be exist when getting them`() {
        repository.addItem("1", mockRepositoryTestModel1)
        repository.addItem("2", mockRepositoryTestModel2)
        repository.addItem("3", mockRepositoryTestModel3)
        repository.addItem("4", mockRepositoryTestModel4)
        repository.addItem("5", mockRepositoryTestModel5)

        assertThat(repository.getItemsByKey("1"), Matchers.equalTo(mockRepositoryTestModel1))
        assertThat(repository.getItemsByKey("2"), Matchers.equalTo(mockRepositoryTestModel2))
        assertThat(repository.getItemsByKey("3"), Matchers.equalTo(mockRepositoryTestModel3))
        assertThat(repository.getItemsByKey("4"), Matchers.equalTo(mockRepositoryTestModel4))
        assertThat(repository.getItemsByKey("5"), Matchers.equalTo(mockRepositoryTestModel5))

        assertThat(
              repository.getAllItems(), Matchers.equalTo(
              listOf(
                    mockRepositoryTestModel1,
                    mockRepositoryTestModel2,
                    mockRepositoryTestModel3,
                    mockRepositoryTestModel4,
                    mockRepositoryTestModel5
              )
        )
        )
    }

    @Test
    fun `When removing items from a page then item should not be exist anymore`() {
        repository.addItems(
              mapOf(
                    ("1" to mockRepositoryTestModel1),
                    ("2" to mockRepositoryTestModel2),
                    ("3" to mockRepositoryTestModel3),
                    ("4" to mockRepositoryTestModel4),
                    ("5" to mockRepositoryTestModel5)
              )
        )

        repository.removeItemByKey("1")
        assertThat(repository.getItemsByKey("1"), Matchers.nullValue())

        repository.removeItemByKey("5")
        assertThat(repository.getItemsByKey("5"), Matchers.nullValue())

        repository.removeItemByKey("3")
        assertThat(repository.getItemsByKey("3"), Matchers.nullValue())
    }

    @Test
    fun `check purge items`() {
        repository.addItems(
              mapOf(
                    ("1" to mockRepositoryTestModel1),
                    ("2" to mockRepositoryTestModel2),
                    ("3" to mockRepositoryTestModel3)
              )
        )

        repository.addItem("4", mockRepositoryTestModel4)
        repository.addItem("5", mockRepositoryTestModel5)

        repository.purgeCache()

        assertThat(repository.getItemsByKey("1"), Matchers.nullValue())
        assertThat(repository.getItemsByKey("2"), Matchers.nullValue())
        assertThat(repository.getItemsByKey("3"), Matchers.nullValue())
        assertThat(repository.getItemsByKey("4"), Matchers.nullValue())
        assertThat(repository.getItemsByKey("5"), Matchers.nullValue())

        assertThat(repository.getAllItems(), Matchers.equalTo(emptyList()))
    }

    data class RepositoryTestModel(val id: String = "0", val name: String = "test")

    class InMemoryTestRepository : InMemoryRepository<String, RepositoryTestModel>()
}