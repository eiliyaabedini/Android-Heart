package de.lizsoft.heart.common.implementation.lifecycle

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import org.junit.Test
import org.koin.core.scope.Scope

class ForegroundActivityServiceImplTest {

    private val mockScopeA: Scope = mock()
    private val mockScopeB: Scope = mock()

    private val screenBucketModelA: ScreenBucketModel = mock {
        on { this.scope } doReturn mockScopeA
    }
    private val activityA: ActivityWithPresenterInterface = mock {
        on { this.getCurrentScreenBucketModel() } doReturn screenBucketModelA
    }

    private val screenBucketModelB: ScreenBucketModel = mock {
        on { this.scope } doReturn mockScopeB
    }
    private val activityB: ActivityWithPresenterInterface = mock {
        on { this.getCurrentScreenBucketModel() } doReturn screenBucketModelB
    }

    private val service: ForegroundActivityServiceImpl =
          ForegroundActivityServiceImpl(mock())

    @Test
    fun `when showing A activity and request with correct scope should return A activity`() {
        service.resumeForTest(activityA)

        service.getResumedScopedActivity(mockScopeA)
              .test()
              .assertComplete()
              .assertNoErrors()
              .assertValue(activityA)
    }

    @Test
    fun `when showing A activity and request with not correct scope should return A activity`() {
        service.resumeForTest(activityA)

        service.getResumedScopedActivity(mockScopeB)
              .test()
              .assertNotComplete()
              .assertNoErrors()
              .assertValueCount(0)
    }

    @Test
    fun `when showing A and then B activity and request with correct scope should return B activity`() {
        service.resumeForTest(activityA)
        service.pauseForTest()
        service.resumeForTest(activityB)

        service.getResumedScopedActivity(mockScopeB)
              .test()
              .assertComplete()
              .assertNoErrors()
              .assertValue(activityB)
    }
}