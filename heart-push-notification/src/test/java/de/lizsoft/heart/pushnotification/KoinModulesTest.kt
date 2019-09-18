package de.lizsoft.heart.pushnotification

import com.nhaarman.mockitokotlin2.mock
import de.lizsoft.heart.interfaces.common.ForegroundActivityService
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.koin.Qualifiers
import de.lizsoft.heart.interfaces.pushnotification.InternalPushNotificationManager
import de.lizsoft.heart.pushnotification.di.heartPushNotificationModule
import de.lizsoft.heart.testhelper.TestReactiveTransformer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import org.koin.test.mock.declare

class KoinModulesTest : KoinTest {

    @Before
    fun setUp() {
        startKoin {
            modules(
                  listOf(
                        heartPushNotificationModule
                  )
            )
        }

        declare { factory<ReactiveTransformer> { TestReactiveTransformer() } }
        declare { factory<ForegroundActivityService> { mock() } }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testHeartPushNotificationModule() {
        testScopedInjection<InternalPushNotificationManager>(Qualifiers.authenticatedUser)
    }

    private inline fun <reified T> testInjection(
          isSingleton: Boolean,
          qualifier: Qualifier? = null
    ) {
        val injectedClassInstance: T by if (qualifier != null) inject<T>(qualifier) else inject()
        val getInjectedClassInstance: T = if (qualifier != null) get(qualifier) else get()

        assertNotNull(injectedClassInstance)

        if (isSingleton) {
            assertEquals(injectedClassInstance, getInjectedClassInstance)
        } else {
            assertNotEquals(injectedClassInstance, getInjectedClassInstance)
        }
    }

    private inline fun <reified S, reified T> testScopedInjection() {
        val injectedClassInstance: T by getKoin().getOrCreateScope(named<S>().toString(), named<S>()).inject()
        val getInjectedClassInstance: T = getKoin().getOrCreateScope(named<S>().toString(), named<S>()).get()

        assertNotNull(injectedClassInstance)

        assertEquals(injectedClassInstance, getInjectedClassInstance)
    }

    private inline fun <reified T> testScopedInjection(qualifier: Qualifier) {
        val injectedClassInstance: T by getKoin().getOrCreateScope(qualifier.toString(), qualifier).inject()
        val getInjectedClassInstance: T = getKoin().getOrCreateScope(qualifier.toString(), qualifier).get()

        assertNotNull(injectedClassInstance)

        assertEquals(injectedClassInstance, getInjectedClassInstance)
    }
}