package de.lizsoft.heart.authentication.di

import de.lizsoft.heart.authentication.FirebaseAuthenticationManagerImpl
import de.lizsoft.heart.authentication.FirebaseCurrentUserManagerImp
import de.lizsoft.heart.interfaces.authentication.FirebaseAuthenticationManager
import de.lizsoft.heart.interfaces.authentication.FirebaseCurrentUserManager
import org.koin.core.module.Module
import org.koin.dsl.module

val heartAuthenticationModule: Module = module {

    single<FirebaseCurrentUserManager> { FirebaseCurrentUserManagerImp() }

    single<FirebaseAuthenticationManager> { FirebaseAuthenticationManagerImpl(get(), get(), get()) }
}