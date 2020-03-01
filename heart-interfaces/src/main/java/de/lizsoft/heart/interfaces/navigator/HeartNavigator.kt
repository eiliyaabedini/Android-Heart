package de.lizsoft.heart.interfaces.navigator

import de.lizsoft.heart.interfaces.common.Navigator

interface HeartNavigator {

    fun <T> registerNavigation(
        clazz: Class<T>,
        navigatorForRegister: (Navigator, T) -> ActivityLauncherOpen
    )

    fun <T : Any> getLauncher(model: T): ActivityLauncherOpen?
}
