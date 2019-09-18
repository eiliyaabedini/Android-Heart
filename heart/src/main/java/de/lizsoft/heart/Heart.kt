package de.lizsoft.heart

import de.lizsoft.heart.common.di.heartCommonModule
import de.lizsoft.heart.common.implementation.di.heartCommonImplementationModule
import de.lizsoft.heart.common.implementation.di.heartCommonImplementationModuleWithParams
import de.lizsoft.heart.common.ui.di.heartCommonUiModule
import de.lizsoft.heart.di.heartModule
import de.lizsoft.heart.interfaces.koin.Qualifiers
import de.lizsoft.heart.maptools.di.heartMapUtilsModule
import de.lizsoft.heart.maptools.ui.di.heartMapUtilsUiModule
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinComponent
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.module

object Heart : KoinComponent {


    //This has to be called after calling StartKoin in APP
    fun bind(
          baseUrl: String? = null,
          modules: List<Module> = emptyList(),
          isTesting: Boolean = false
    ) {
        val listOfModules: List<Module> = listOf(
              heartModule,
              heartCommonModule,
              heartCommonUiModule,
              heartMapUtilsModule,
              heartMapUtilsUiModule,
              heartCommonImplementationModule,
              heartCommonImplementationModuleWithParams(baseUrl)
        )

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                if (isTesting.not() && BuildConfig.DEBUG) androidLogger(Level.DEBUG)
                modules(
                      listOfModules + modules
                )
            }
        } else {
            loadKoinModules(
                  listOfModules + modules
            )
        }

        //TODO check if other modules that required Firebase are also added to this project then run:
        //FirebaseApp.initializeApp(application.applicationContext)
    }

    fun addOkHttpInterceptors(vararg interceptors: Interceptor) {
        val heartAddOkHttpInterceptorsModule: Module = module {
            val okHttpClient: OkHttpClient = get(Qualifiers.noCachingApiOKHTTP)
            factory<OkHttpClient>(Qualifiers.noCachingApiOKHTTP, override = true) {
                okHttpClient
                      .newBuilder()
                      .apply {
                          interceptors.forEach { interceptor ->
                              addInterceptor(interceptor)
                          }
                      }
                      .build()
            }
        }

        loadKoinModules(heartAddOkHttpInterceptorsModule)
    }
}