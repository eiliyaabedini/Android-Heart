package de.lizsoft.heart.common.ui.navigator

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import de.lizsoft.heart.activitylauncher.ActivityLauncher
import de.lizsoft.heart.common.ui.ui.dialogActivity.DialogActivity
import de.lizsoft.heart.interfaces.common.Navigator
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import de.lizsoft.heart.interfaces.dialog.DialogActivityModel

class NavigatorImp : Navigator {

    private lateinit var activity: ActivityWithPresenterInterface

    override fun setActivity(activity: ActivityWithPresenterInterface) {
        this.activity = activity
    }

    override fun openDialogScreen(model: DialogActivityModel) {
        ActivityLauncher.with(activity).open(DialogActivity::class.java)
              .addArgument(model)
              .startActivity()
    }

    override fun getActivity(): ActivityWithPresenterInterface {
        return this.activity
    }

    override fun restartAPP() {
        //        val intent = Intent(activity.getContext(), LandingActivity::class.java)
        val intent = Intent("android.intent.action.MAIN")
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        //        intent.putExtra(KEY_RESTART_INTENT, nextIntent)
        activity.getContext().startActivity(intent)
        if (activity.getContext() is Activity) {
            (activity.getContext() as Activity).finish()
        }

        Runtime.getRuntime().exit(0)
    }

    override fun openUrl(url: String) {
        activity.getContext().startActivity(
              Intent(Intent.ACTION_VIEW).apply {
                  data = Uri.parse(url)
              }
        )
    }
}