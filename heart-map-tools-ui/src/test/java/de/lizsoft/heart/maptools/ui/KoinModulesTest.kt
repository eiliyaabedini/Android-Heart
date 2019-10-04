package de.lizsoft.heart.maptools.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import de.lizsoft.heart.interfaces.koin.Qualifiers
import de.lizsoft.heart.interfaces.map.service.AddressService
import de.lizsoft.heart.interfaces.map.service.CurrentLocation
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import de.lizsoft.heart.maptools.ui.di.heartMapUtilsUiModule
import de.lizsoft.heart.maptools.ui.search.SearchAddressActivity
import de.lizsoft.heart.maptools.ui.search.SearchAddressPresenter
import de.lizsoft.heart.maptools.ui.search.SelectAddressOnMapActivity
import de.lizsoft.heart.maptools.ui.search.SelectAddressOnMapPresenter
import de.lizsoft.heart.testhelper.TestReactiveTransformer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import org.koin.test.mock.declare

class KoinModulesTest : KoinTest {

    private val mockApplication: Application = mock()
    private val mockContext: Context = mock()

    private val applicationModule: Module = module {
        single(Qualifiers.applicationContext) { mockContext }
        single(Qualifiers.applicationInstance) { mockApplication }
    }

    @Before
    fun setUp() {
        startKoin {
            modules(
                  listOf(
                        applicationModule,
                        heartMapUtilsUiModule
                  )
            )
        }

        declare { factory<ReactiveTransformer> { TestReactiveTransformer() } }
        declare { factory<CurrentLocation> { mock() } }
        declare { factory<AddressService> { mock() } }
        declare { factory<SharedPreferences> { mock() } }
        declare { factory<Gson> { mock() } }
        declare { factory<FirebaseRemoteConfig> { mock() } }
        whenever(get<FirebaseRemoteConfig>().fetchAndActivate()).thenReturn(mock())
        declare { factory<FirebaseAnalytics> { mock() } }
        declare { factory<HeartNavigator> { mock() } }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testHeartMapUtilsUiModule() {
        testScopedInjection<SearchAddressActivity, Presenter<SearchAddressPresenter.View>>()
        testScopedInjection<SelectAddressOnMapActivity, Presenter<SelectAddressOnMapPresenter.View>>()
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