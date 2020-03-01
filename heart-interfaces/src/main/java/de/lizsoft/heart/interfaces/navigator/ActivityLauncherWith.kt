package de.lizsoft.heart.interfaces.navigator

import android.content.Context
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface

class ActivityLauncherWith internal constructor(
    private val activity: ActivityWithPresenterInterface? = null,
    private val context: Context = activity!!.getContext()
) {

    fun open(clazz: Class<*>): ActivityLauncherOpen {
        return ActivityLauncherOpenImp(activity, context, clazz)
    }
}
