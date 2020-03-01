package de.lizsoft.heart.interfaces.common

import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import de.lizsoft.heart.interfaces.dialog.DialogActivityModel
import de.lizsoft.heart.interfaces.navigator.ActivityLauncherOpen

interface Navigator {

    fun setActivity(activity: ActivityWithPresenterInterface)
    fun getActivity(): ActivityWithPresenterInterface

    fun getDialogScreen(model: DialogActivityModel): ActivityLauncherOpen

    fun restartAPP()

    fun openUrl(url: String)
}
