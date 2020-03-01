package de.lizsoft.heart

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.lizsoft.heart.common.di.heartCommonModule
import de.lizsoft.heart.common.event.EventManager
import de.lizsoft.heart.common.implementation.di.heartCommonImplementationModule
import de.lizsoft.heart.common.implementation.di.heartCommonImplementationModuleWithParams
import de.lizsoft.heart.common.implementation.firebase.remote.FirebaseRemoteConfigInitializer
import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.common.ui.di.heartCommonUiModule
import de.lizsoft.heart.common.ui.factory.DialogFactory
import de.lizsoft.heart.common.ui.ui.dialogActivity.DialogActivity
import de.lizsoft.heart.common.ui.ui.dialogActivity.DialogActivityPresenter
import de.lizsoft.heart.di.heartModule
import de.lizsoft.heart.errorhandler.GenericErrorHandler
import de.lizsoft.heart.errorhandler.di.heartErrorHandlerModule
import de.lizsoft.heart.interfaces.common.*
import de.lizsoft.heart.interfaces.common.event.EventTracker
import de.lizsoft.heart.interfaces.common.event.FirebaseAnalyticsLogger
import de.lizsoft.heart.interfaces.common.firebase.messaging.FirebaseMessagingDelegate
import de.lizsoft.heart.interfaces.common.firebase.remote.FirebaseRemoteConfigDelegate
import de.lizsoft.heart.interfaces.koin.Qualifiers
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
import org.koin.test.mock.declareMock

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
                        heartCommonUiModule,
                        heartCommonImplementationModule,
                        heartCommonModule,
                        heartModule,
                        heartCommonImplementationModuleWithParams(""),
                        heartErrorHandlerModule { }
                  )
            )
        }

        declare { factory<ReactiveTransformer> { TestReactiveTransformer() } }

        declareMock<SharedPreferences>()
        declareMock<Gson>()
        declareMock<FirebaseRemoteConfig>()
        whenever(get<FirebaseRemoteConfig>().fetchAndActivate()).thenReturn(mock())
        declareMock<FirebaseAnalytics>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testApplicationModule() {
        testInjection<Context>(true, Qualifiers.applicationContext)
        testInjection<Application>(true, Qualifiers.applicationInstance)
        testInjection<Navigator>(true)
    }

    @Test
    fun testHeartCommonModule() {
        testInjection<EventManager>(true)
    }

    @Test
    fun testHeartErrorHandlerModule() {
        testInjection<GenericErrorHandler>(true)
    }

    @Test
    fun testHeartCommonImplementationModule() {
        testInjection<ForegroundActivityService>(true)
        testInjection<FirebaseMessagingDelegate>(false)
        testInjection<LocalStorageManager>(true)
        testInjection<FirebaseRemoteConfig>(true)
        testInjection<FirebaseRemoteConfigDelegate>(true)
        testInjection<FirebaseRemoteConfigInitializer>(true)
        testInjection<FirebaseAnalyticsLogger>(true)
        testInjection<EventTracker>(true)
        testInjection<PermissionHandler>(false)
        testInjection<String>(true, Qualifiers.baseApiUrl)
    }

    @Test
    fun testHeartCommonUiModule() {
        testScopedInjection<DialogActivity, Presenter<DialogActivityPresenter.View>>()
        testInjection<TextUtils>(false)
        testInjection<ColorUtils>(false)
        testInjection<DrawableUtils>(false)
        testInjection<DialogFactory>(false)
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
