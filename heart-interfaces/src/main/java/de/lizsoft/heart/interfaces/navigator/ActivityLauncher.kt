package de.lizsoft.heart.interfaces.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import java.io.Serializable

class ActivityLauncher private constructor() {

    companion object {
        fun with(activity: ActivityWithPresenterInterface): ActivityLauncherWith {
            return ActivityLauncherWith(activity = activity)
        }

        fun with(context: Context): ActivityLauncherWith {
            return ActivityLauncherWith(context = context)
        }
    }
}

inline fun <reified T : Serializable> Activity.retrieveArgument(newIntent: Intent? = null): T? {
    return (newIntent ?: intent).extras?.getSerializable(T::class.java.simpleName) as? T
}
