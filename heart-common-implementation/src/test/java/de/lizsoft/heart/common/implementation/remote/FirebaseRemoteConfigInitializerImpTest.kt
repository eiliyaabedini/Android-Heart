package de.lizsoft.heart.common.implementation.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import de.lizsoft.heart.common.implementation.firebase.remote.FirebaseRemoteConfigDelegateImp
import de.lizsoft.heart.common.implementation.firebase.remote.FirebaseRemoteConfigInitializer
import de.lizsoft.heart.interfaces.common.rx.ToSingle
import org.junit.Test

class FirebaseRemoteConfigDelegateImpTest {

    private val mockRemoteConfig: FirebaseRemoteConfig = mock {
        on { getString("TestString") } doReturn RESULT_STRING
        on { getBoolean("TestBoolean") } doReturn RESULT_BOOLEAN
    }

    private val remoteConfigInitializer: FirebaseRemoteConfigInitializer =
          mock {
              on { updateRemoteConfig() } doReturn mockRemoteConfig.ToSingle()
          }

    private val delegate: FirebaseRemoteConfigDelegateImp =
          FirebaseRemoteConfigDelegateImp(
                remoteConfigInitializer
          )

    @Test
    fun `when requested for string TestString then return the correct value`() {
        delegate.getTestString()
              .test()
              .assertValueCount(1)
              .assertValue(RESULT_STRING)
              .assertNoErrors()
              .assertComplete()
    }

    @Test
    fun `when requested for string TestBoolean then return the correct value`() {
        delegate.getTestBoolean()
              .test()
              .assertValueCount(1)
              .assertValue(RESULT_BOOLEAN)
              .assertNoErrors()
              .assertComplete()
    }

    companion object {
        private const val RESULT_STRING: String = "abcddd"
        private const val RESULT_BOOLEAN: Boolean = true
    }
}