package de.lizsoft.heart.interfaces.navigator

import io.reactivex.Maybe
import de.lizsoft.heart.interfaces.common.Navigator

interface HeartNavigator {

    fun <T> registerNavigation(clazz: Class<T>, navigatorForRegister: (Navigator, T) -> Unit)

    fun <T, R> registerMaybeNavigation(clazz: Class<T>, navigatorForRegister: (Navigator, T) -> Maybe<R>)

    fun <T: Any> navigate(model: T)

    fun <T: Any, R> navigateMaybe(model: T): Maybe<R>
}