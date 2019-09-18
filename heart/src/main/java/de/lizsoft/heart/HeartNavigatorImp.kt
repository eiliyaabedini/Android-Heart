package de.lizsoft.heart

import de.lizsoft.heart.interfaces.common.Navigator
import de.lizsoft.heart.interfaces.navigator.HeartNavigator
import io.reactivex.Maybe

internal class HeartNavigatorImp(
      val navigator: Navigator
) : HeartNavigator {

    private val userNavigators: MutableMap<Any, (Any) -> Unit> = mutableMapOf()
    private val userMaybeNavigators: MutableMap<Any, (Any) -> Maybe<*>> = mutableMapOf()

    override fun <T> registerNavigation(clazz: Class<T>, navigatorForRegister: (Navigator, T) -> Unit) {
        userNavigators[clazz] = { model ->
            navigatorForRegister(navigator, model as T)
        }
    }

    override fun <T, R> registerMaybeNavigation(clazz: Class<T>, navigatorForRegister: (Navigator, T) -> Maybe<R>) {
        userMaybeNavigators[clazz] = { model ->
            navigatorForRegister(navigator, model as T)
        }
    }

    override fun <T : Any> navigate(model: T) {
        userNavigators[model::class.java]?.invoke(model)
    }

    override fun <T : Any, R> navigateMaybe(model: T): Maybe<R> {
        return userMaybeNavigators[model::class.java]?.invoke(model) as Maybe<R>
    }
}