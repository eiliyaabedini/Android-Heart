package de.lizsoft.heart

import de.lizsoft.heart.interfaces.common.Navigator
import de.lizsoft.heart.interfaces.navigator.ActivityLauncherOpen
import de.lizsoft.heart.interfaces.navigator.HeartNavigator

internal class HeartNavigatorImp(
    private val navigator: Navigator
) : HeartNavigator {

    private val userNavigators: MutableMap<Any, (Any) -> ActivityLauncherOpen> = mutableMapOf()

    override fun <T> registerNavigation(
        clazz: Class<T>,
        navigatorForRegister: (Navigator, T) -> ActivityLauncherOpen
    ) {
        userNavigators[clazz] = { model ->
            navigatorForRegister(navigator, model as T)
        }
    }

    override fun <T : Any> getLauncher(model: T): ActivityLauncherOpen? {
        return userNavigators[model::class.java]?.invoke(model)
    }
}
