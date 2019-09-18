package de.lizsoft.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import de.lizsoft.heart.Heart
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.koin.Qualifiers
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import de.lizsoft.heart.maptools.ui.HeartMap
import de.lizsoft.heart.testhelper.TestReactiveTransformer
import de.lizsoft.travelcheck.di.travelCheckModule
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import org.koin.test.mock.declare
import org.koin.test.mock.declareMock
import retrofit2.Retrofit

class KoinModulesTest : KoinTest {

    private val mockApplication: Application = mock()
    private val mockContext: Context = mock()

    private val applicationModule: Module = module {
        single(Qualifiers.applicationContext) { mockContext }
        single(Qualifiers.applicationInstance) { mockApplication }
    }

    @Before
    fun setUp() {
        Heart.bind(
              baseUrl = "https://www.google.com/",
              modules = listOf(
                    applicationModule,
                    travelCheckModule
              ),
              isTesting = true
        )
        HeartMap.register(get())

        declare { factory<ReactiveTransformer> { TestReactiveTransformer() } }

        declareMock<HeartNavigator>()
        declareMock<SharedPreferences>()
        declareMock<Gson>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testApplicationModule() {
        testInjection<Context>(true, Qualifiers.applicationContext)
        testInjection<Application>(true, Qualifiers.applicationInstance)
    }


    @Test
    fun testServicesModule() {
        testInjection<OkHttpClient>(false, Qualifiers.noCachingApiOKHTTP)
        testInjection<Retrofit>(true, Qualifiers.noCachingApiRETROFIT)
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