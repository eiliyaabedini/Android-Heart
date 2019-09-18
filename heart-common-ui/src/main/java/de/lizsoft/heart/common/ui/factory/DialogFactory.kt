package de.lizsoft.heart.common.ui.factory

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import de.lizsoft.heart.common.ui.R
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import io.reactivex.Maybe
import io.reactivex.subjects.MaybeSubject
import java.sql.Time
import java.util.*

class DialogFactory {

    fun makeItemsDialog(
          context: Context,
          title: String,
          vararg items: Pair<String, () -> Unit>,
          finally: () -> Unit = {}
    ) {
        val itemNames = items.map { it.first }.toTypedArray()

        AlertDialog.Builder(context)
              .setTitle(title)
              .setItems(itemNames) { dialog, item ->
                  dialog.dismiss()

                  items[item].second()

                  finally()
              }
              .setOnCancelListener {
                  finally()
              }
              .show()
    }

    fun makeMultiSelectItemsDialog(
          context: Context,
          title: String,
          vararg items: Pair<String, Boolean>,
          finally: (List<String>) -> Unit = {}
    ) {
        val itemNames = items.map { it.first }.toTypedArray()
        val itemSelected = items.map { it.second }.toBooleanArray()

        val selectedItems: MutableList<String> = mutableListOf()

        AlertDialog.Builder(context)
              .setTitle(title)
              .setMultiChoiceItems(itemNames, itemSelected) { _, item, isChecked ->
                  if (isChecked) {
                      selectedItems.add(items[item].first)
                  } else {
                      selectedItems.remove(items[item].first)
                  }
              }
              .setPositiveButton("OK") { _, _ ->
                  finally(selectedItems)
              }
              .setNegativeButton("Cancel") { _, _ -> }
              .show()
    }

    fun makeRadioItemsDialog(
          context: Context,
          vararg items: Pair<String, () -> Unit>
    ) {
        val itemNames = items.map { it.first }.toTypedArray()

        AlertDialog.Builder(context)
              .setTitle("Select")
              .setSingleChoiceItems(itemNames, 2) { _, _ ->
                  //            items[which].second()
              }
              .setPositiveButton(R.string.common_ok) { dialog, _ ->
                  dialog.dismiss()

                  val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition
                  items[selectedPosition].second()
              }
              .setNegativeButton(R.string.common_cancel) { dialog, _ ->
                  dialog.dismiss()
              }
              .show()
    }

    fun showNormalDialog(context: Context, @StringRes messageId: Int) {
        AlertDialog.Builder(context)
              .setMessage(messageId)
              .setPositiveButton(R.string.common_ok, null)
              .show()
    }

    fun showDatePickerDialog(
          context: Context,
          defaultCalendar: Calendar? = null,
          isPossibleToSelectPastDates: Boolean = false,
          reactiveTransformer: ReactiveTransformer
    ): Maybe<Date> {
        val maybeSubject: MaybeSubject<Date> = MaybeSubject.create()

        val calendar = defaultCalendar ?: Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
              context,
              { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                  val selectedCalendar = Calendar.getInstance()
                  selectedCalendar.set(
                        selectedYear,
                        selectedMonth,
                        selectedDayOfMonth
                  )
                  maybeSubject.onSuccess(selectedCalendar.time)
              },
              year,
              month,
              day
        )

        if (!isPossibleToSelectPastDates) {
            dialog.datePicker.minDate = System.currentTimeMillis() - 1000 //1 Sec
        }

        dialog.setOnCancelListener { maybeSubject.onComplete() }
        dialog.setOnDismissListener { maybeSubject.onComplete() }

        return maybeSubject
              .subscribeOn(reactiveTransformer.mainThreadScheduler())
              .doOnSubscribe { dialog.show() }
    }

    fun showTimePickerDialog(
          context: Context,
          defaultCalendar: Calendar? = null,
          reactiveTransformer: ReactiveTransformer
    ): Maybe<Time> {
        val maybeSubject: MaybeSubject<Time> = MaybeSubject.create()

        val calendar = defaultCalendar ?: Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val dialog = TimePickerDialog(
              context,
              { _: TimePicker, selectedHourOfDay: Int, selectedMinute: Int ->

                  val cal = Calendar.getInstance()
                  cal.set(Calendar.HOUR_OF_DAY, selectedHourOfDay)
                  cal.set(Calendar.MINUTE, selectedMinute)

                  maybeSubject.onSuccess(Time(cal.timeInMillis))
              },
              hour,
              minute,
              true
        )

        dialog.setOnCancelListener { maybeSubject.onComplete() }
        dialog.setOnDismissListener { maybeSubject.onComplete() }

        return maybeSubject
              .subscribeOn(reactiveTransformer.mainThreadScheduler())
              .doOnSubscribe { dialog.show() }
    }
}