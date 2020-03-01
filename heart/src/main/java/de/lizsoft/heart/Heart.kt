package de.lizsoft.heart

import android.app.Application
import android.content.Context
import de.lizsoft.heart.common.di.heartCommonModule
import de.lizsoft.heart.common.implementation.di.heartCommonImplementationModule
import de.lizsoft.heart.common.implementation.di.heartCommonImplementationModuleWithParams
import de.lizsoft.heart.common.ui.di.heartCommonUiModule
import de.lizsoft.heart.di.heartModule
import de.lizsoft.heart.interfaces.koin.Qualifiers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
          application: Application,
          baseUrl: String? = null,
          modules: List<Module> = emptyList(),
          isTesting: Boolean = false
    ) {

        val applicationModule: Module = module {
            single<Context>(Qualifiers.applicationContext) { application.applicationContext }
            single(Qualifiers.applicationInstance) { application }
        }

        val listOfModules: MutableList<Module> = mutableListOf(
              applicationModule,
              heartModule,
              heartCommonModule,
              heartCommonUiModule,
              heartCommonImplementationModule
        )

        if (baseUrl != null) listOfModules.add(heartCommonImplementationModuleWithParams(baseUrl))

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                if (isTesting) androidLogger(Level.DEBUG)
                modules(
                      listOfModules + modules
                )
            }
        } else {
            loadKoinModules(
                  listOfModules + modules
            )
        }
    }

    fun addOkHttpInterceptors(
          interceptors: List<Interceptor> = emptyList(),
          debugLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
    ) {
        val heartAddOkHttpInterceptorsModule: Module = module {
            val okHttpClient: OkHttpClient = get(Qualifiers.noCachingApiOKHTTP)
            single<OkHttpClient>(Qualifiers.noCachingApiOKHTTP, override = true) {
                okHttpClient
                      .newBuilder()
                      .apply {
                          interceptors.forEach { interceptor ->
                              addInterceptor(interceptor)
                          }

                          addInterceptor(
                                HttpLoggingInterceptor().apply {
                                    level = debugLevel
                                }
                          )
                      }
                      .build()
            }
        }

        loadKoinModules(heartAddOkHttpInterceptorsModule)
    }
}
