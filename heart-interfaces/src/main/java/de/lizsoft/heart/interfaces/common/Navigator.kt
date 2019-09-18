package de.lizsoft.heart.interfaces.common

import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import de.lizsoft.heart.interfaces.dialog.DialogActivityModel

interface Navigator {

    fun setActivity(activity: ActivityWithPresenterInterface)
    fun getActivity(): ActivityWithPresenterInterface

    fun openDialogScreen(model: DialogActivityModel)

    fun restartAPP()

    fun openUrl(url: String)
}