package de.lizsoft.heart.interfaces.navigator

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
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

interface ActivityLauncherOpen {
    fun closeOtherActivities(): ActivityLauncherOpen
    fun <T : Serializable> addArgument(argument: T): ActivityLauncherOpen
    fun addTransitionView(currentActivity: Activity, view: View, sharedElementName: String): ActivityLauncherOpen
    fun addTheOnlyParent(parent: Class<*>): ActivityLauncherOpen
    fun getPendingIntent(): PendingIntent
    fun getIntent(): Intent
    fun startActivity()
    fun startActivityForResult(): Maybe<Serializable>
}

internal class ActivityLauncherOpenImp internal constructor(
    private val activity: ActivityWithPresenterInterface? = null,
    private val context: Context = activity!!.getContext(),
    private val clazz: Class<*>
) : ActivityLauncherOpen {

    private var options: ActivityOptionsCompat? = null
    private var parent: Class<*>? = null
    private var intent: Intent = Intent(context, clazz)

    override fun closeOtherActivities(): ActivityLauncherOpen {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return this
    }

    override fun <T : Serializable> addArgument(argument: T): ActivityLauncherOpen {
        intent.putExtra(argument::class.java.simpleName, argument)
        return this
    }

    override fun addTransitionView(currentActivity: Activity, view: View, sharedElementName: String): ActivityLauncherOpen {
        view.transitionName = sharedElementName
        options = ActivityOptionsCompat.makeSceneTransitionAnimation(currentActivity, view, sharedElementName)
        return this
    }

    override fun addTheOnlyParent(parent: Class<*>): ActivityLauncherOpen {
        this.parent = parent

        return this
    }

    override fun getPendingIntent(): PendingIntent {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(
              context, 0, intent,
              PendingIntent.FLAG_ONE_SHOT
        )
    }

    override fun getIntent(): Intent = intent

    override fun startActivity() {
        when {
            options != null -> activity!!.startActivityFromParent(intent, options!!.toBundle())
            parent != null -> TaskStackBuilder.create(context)
                  .addNextIntentWithParentStack(Intent(context, parent))
                  .addNextIntent(intent)
                  .startActivities()
            else -> activity!!.startActivityFromParent(intent)
        }
    }

    override fun startActivityForResult(): Maybe<Serializable> {

        val requestCode = LAST_REQUESTED_CODE++

        if (options == null) {
            activity!!.startActivityForResultFromParent(intent, requestCode)
        } else {
            activity!!.startActivityForResultFromParent(intent, requestCode, options!!.toBundle())
        }

        return activity.observeActivityResult()
              .filter { it.requestCode == requestCode }
              .filter { it.data?.hasExtra(ActivityWithPresenterInterface.RESULT_INTENT_KEY) ?: false }
              .map { it.data?.getSerializableExtra(ActivityWithPresenterInterface.RESULT_INTENT_KEY).toOptional() }
              .filter { it.isSome }
              .map { it.get() }
              .firstElement()
    }

    companion object {
        private var LAST_REQUESTED_CODE: Int = 100
    }
}
