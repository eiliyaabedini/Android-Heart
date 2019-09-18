package de.lizsoft.heart.maptools.ui.di

import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.maptools.ui.search.SearchAddressActivity
import de.lizsoft.heart.maptools.ui.search.SearchAddressPresenter
import de.lizsoft.heart.maptools.ui.search.SelectAddressOnMapActivity
import de.lizsoft.heart.maptools.ui.search.SelectAddressOnMapPresenter
import org.koin.core.qualifier.named
import org.koin.dsl.module

val heartMapUtilsUiModule = module {
    scope(named<SearchAddressActivity>()) {
        scoped<Presenter<SearchAddressPresenter.View>> {
            SearchAddressPresenter(get(), get(), get(), get())
        }
    }

    scope(named<SelectAddressOnMapActivity>()) {
        scoped<Presenter<SelectAddressOnMapPresenter.View>> {
            SelectAddressOnMapPresenter(get())
        }
    }
}