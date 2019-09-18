package de.lizsoft.heart.interfaces.common.presenter

import androidx.annotation.StringRes
import de.lizsoft.heart.interfaces.common.Navigator
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import io.reactivex.Maybe
import io.reactivex.Observable
import org.koin.core.scope.Scope
import java.io.Serializable
import java.sql.Time
import java.util.*

interface PresenterCommonView : Navigator {
    fun actions(): Observable<PresenterAction>
    fun passActionInternal(action: PresenterAction)

    fun getCurrentScope(): Scope

    fun showContent()
    fun showContentLoading()
    fun showContentEmpty()
    fun showContentError()

    //    fun showDialogLoading()
    //    fun showDialogError()
    //    fun showDialogSuccessAndCloseScreen()
    //    fun showDialogSuccess(callback: () -> Unit = {})
    //    fun hideDialogOverlay()
    fun makeItemsDialog(
          title: String = "Select",
          vararg items: Pair<String,
                () -> Unit>, finally: () -> Unit = {}
    )

    fun makeMultiSelectItemsDialog(
          title: String = "Select",
          vararg items: Pair<String, Boolean>,
          finally: (List<String>) -> Unit = {}
    )

    fun makeRadioItemsDialog(vararg items: Pair<String, () -> Unit>)
    fun showNormalDialog(@StringRes messageId: Int)
    fun showDialogDatePicker(
          defaultDate: Date?,
          isPossibleToSelectPastDates: Boolean = true
    ): Maybe<Date>

    fun showDialogTimePicker(defaultTime: Date = Calendar.getInstance().time): Maybe<Time>
    fun showDialogDateTimePicker(): Maybe<Date>

    fun showSnackBar(message: String)

    fun callPhoneNumber(phoneNumber: String)
    fun sendSmsToPhoneNumber(phoneNumber: String)
    fun showCallAndSmsBottomSheet(phoneNumber: String)

    fun closeScreenWithResult(serializable: Serializable)
    fun closeScreen()

    //Prevent overriding it in activity
    override fun setActivity(activity: ActivityWithPresenterInterface) {

    }
}