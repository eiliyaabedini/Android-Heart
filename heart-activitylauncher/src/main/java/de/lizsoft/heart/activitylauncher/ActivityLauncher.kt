package de.lizsoft.heart.activitylauncher

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.TaskStackBuilder
import de.lizsoft.heart.interfaces.common.get
import de.lizsoft.heart.interfaces.common.isSome
import de.lizsoft.heart.interfaces.common.toOptional
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import io.reactivex.Maybe
import java.io.Serializable

class ActivityLauncher private constructor() {

    class ActivityLauncherWith internal constructor(
        private val activity: ActivityWithPresenterInterface
    ) {
        fun open(clazz: Class<*>): ActivityLauncherOpen {
            return ActivityLauncherOpen(activity, clazz)
        }
    }

    class ActivityLauncherOpen internal constructor(
          private val activity: ActivityWithPresenterInterface,
          private val clazz: Class<*>
    ) {

        private var options: ActivityOptionsCompat? = null
        private var parent: Class<*>? = null
        private var intent: Intent = Intent(activity.getContext(), clazz)

        fun closeOtherActivities(): ActivityLauncherOpen {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return this
        }

        fun <T : Serializable> addArgument(argument: T): ActivityLauncherOpen {
            intent.putExtra(argument::class.java.simpleName, argument)
            return this
        }

        fun addTransitionView(currentActivity: Activity, view: View, sharedElementName: String): ActivityLauncherOpen {
            view.transitionName = sharedElementName
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(currentActivity, view, sharedElementName)
            return this
        }

        fun addTheOnlyParent(parent: Class<*>): ActivityLauncherOpen {
            this.parent = parent

            return this
        }

        fun getPendingIntent(): PendingIntent {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return PendingIntent.getActivity(
                  activity.getContext(), 0, intent,
                  PendingIntent.FLAG_ONE_SHOT
            )
        }

        fun getIntent(): Intent = intent

        fun startActivity() {
            when {
                options != null -> activity.startActivityFromParent(intent, options!!.toBundle())
                parent != null -> TaskStackBuilder.create(activity.getContext())
                      .addNextIntentWithParentStack(Intent(activity.getContext(), parent))
                      .addNextIntent(intent)
                      .startActivities()
                else -> activity.startActivityFromParent(intent)
            }
        }

        fun startActivityForResult(): Maybe<Serializable> {

            val requestCode = LAST_REQUESTED_CODE++

            if (options == null) {
                activity.startActivityForResultFromParent(intent, requestCode)
            } else {
                activity.startActivityForResultFromParent(intent, requestCode, options!!.toBundle())
            }

            return activity.observeActivityResult()
                  .filter { it.requestCode == requestCode }
                  .filter { it.data?.hasExtra(ActivityWithPresenterInterface.RESULT_INTENT_KEY) ?: false }
                  .map { it.data?.getSerializableExtra(ActivityWithPresenterInterface.RESULT_INTENT_KEY).toOptional() }
                  .filter { it.isSome }
                  .map { it.get() }
                  .firstElement()
        }
    }

    companion object {
        fun with(activity: ActivityWithPresenterInterface): ActivityLauncherWith {
            return ActivityLauncherWith(activity)
        }

        private var LAST_REQUESTED_CODE: Int = 100
    }
}

inline fun <reified T : Serializable> Activity.retrieveArgument(newIntent: Intent? = null): T? {
    return (newIntent ?: intent).extras?.getSerializable(T::class.java.simpleName) as? T
}