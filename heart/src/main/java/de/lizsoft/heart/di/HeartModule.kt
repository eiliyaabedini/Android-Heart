package de.lizsoft.heart.di

import de.lizsoft.heart.HeartNavigatorImp
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import org.koin.core.module.Module
import org.koin.dsl.module

//TODO make it internal
val heartModule: Module = module {
    single<HeartNavigator> { HeartNavigatorImp(get()) }
}