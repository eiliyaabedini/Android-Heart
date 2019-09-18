package de.lizsoft.heart.deeplink.di

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.deeplink.DeeplinkDispatcher
import de.lizsoft.heart.deeplink.FirebaseDeeplinkFetcherImp
import de.lizsoft.heart.deeplink.LinkDispatcherActivity
import de.lizsoft.heart.deeplink.LinkDispatcherActivityPresenter
import de.lizsoft.heart.interfaces.deeplink.FirebaseDeeplinkFetcher
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val heartDeeplinkModule: Module = module {
    single {
        FirebaseDynamicLinks.getInstance()
    }
    factory { DeeplinkDispatcher(get()) }
    factory<FirebaseDeeplinkFetcher> { FirebaseDeeplinkFetcherImp(get()) }

    scope(named<LinkDispatcherActivity>()) {
        scoped<Presenter<LinkDispatcherActivityPresenter.View>> {
            LinkDispatcherActivityPresenter()
        }
    }
}