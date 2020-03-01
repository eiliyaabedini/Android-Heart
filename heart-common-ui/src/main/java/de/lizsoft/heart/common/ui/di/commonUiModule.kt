package de.lizsoft.heart.common.ui.di

import de.lizsoft.heart.common.presenter.Presenter
import de.lizsoft.heart.common.ui.ColorUtilsImp
import de.lizsoft.heart.common.ui.DateUtilsImp
import de.lizsoft.heart.common.ui.DrawableUtilsImp
import de.lizsoft.heart.common.ui.TextUtilsImp
import de.lizsoft.heart.common.ui.factory.DialogFactory
import de.lizsoft.heart.common.ui.navigator.NavigatorImp
import de.lizsoft.heart.common.ui.ui.dialogActivity.DialogActivity
import de.lizsoft.heart.common.ui.ui.dialogActivity.DialogActivityPresenter
import de.lizsoft.heart.interfaces.common.*
import de.lizsoft.heart.interfaces.koin.Qualifiers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.factory

val heartCommonUiModule = module {
    scope(named<DialogActivity>()) {
        scoped<Presenter<DialogActivityPresenter.View>> {
            DialogActivityPresenter()
        }
    }

    factory<TextUtils> { TextUtilsImp(get(Qualifiers.applicationContext)) }
    factory<DateUtils> { DateUtilsImp(get(Qualifiers.applicationContext)) }
    factory<ColorUtils> { ColorUtilsImp(get(Qualifiers.applicationContext)) }
    factory<DrawableUtils> { DrawableUtilsImp(get(Qualifiers.applicationContext)) }
    factory<DialogFactory>()
    single<Navigator> { NavigatorImp() }
}
