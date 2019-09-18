package de.lizsoft.heart.common.extension

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

fun Date.toTimeString(context: Context): String = time.toTimeString(context)

fun Long.toTimeString(context: Context): String = DateFormat.getTimeFormat(context).format(this)

fun Date.toLongDateString(context: Context): String = time.toLongDateString(context)

fun Date.toSimpleDate(context: Context): String = android.text.format.DateFormat.getDateFormat(context).format(this)

fun Long.toLongDateString(context: Context): String = DateFormat.getLongDateFormat(context).format(this)

fun Date.addWeek(amount: Int = 1): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.WEEK_OF_YEAR, amount)
    return calendar.time
}

fun Date.removeWeek(amount: Int = 1): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.WEEK_OF_YEAR, -amount)
    return calendar.time
}

fun Date.toRelativeDateTimeString(context: Context) = time.toRelativeDateTimeString(context)
fun Date.toRelativeDateString(context: Context, showDay: Boolean = true) = time.toRelativeDateString(context, showDay)

fun Long.toRelativeDateTimeString(context: Context) = "${this.toRelativeDateString(context)}, ${toTimeString(context)}"

/**
 * Formats this long (interpreted as a timestamp) relative to the current time. Resolves to relative dates only for
 * "Yesterday", "Today" and "Tomorrow". If instead the timestamp is further away in the future but no more than seven
 * days in the future, it will resolve to the weekday, e.g. "Sunday". For all other dates we display the
 * weekday and the abbreviated date without the year, e.g. "Tue, Oct 23", respecting the user's regional preferences.
 */
fun Long.toRelativeDateString(context: Context, showDay: Boolean = true): String {
    val midnight = Calendar.getInstance()
    midnight.set(Calendar.HOUR_OF_DAY, 0)
    midnight.set(Calendar.MINUTE, 0)
    midnight.set(Calendar.SECOND, 0)
    midnight.set(Calendar.MILLISECOND, 0)

    val midnightYesterday = midnight.timeInMillis - DateUtils.DAY_IN_MILLIS
    val midnightInTwoDays = midnight.timeInMillis + 2 * DateUtils.DAY_IN_MILLIS
    val midnightInSevenDays = midnight.timeInMillis + 7 * DateUtils.DAY_IN_MILLIS

    return when {
        this in midnightYesterday until midnightInTwoDays -> {
            DateUtils.getRelativeTimeSpanString(
                  this, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS, 0
            ).toString()
        }

        this in midnightInTwoDays until midnightInSevenDays -> {
            SimpleDateFormat("EEEE", Locale.getDefault()).format(this)
        }

        else -> {
            val flags = if (showDay) {
                DateUtils.FORMAT_SHOW_DATE or
                      DateUtils.FORMAT_ABBREV_MONTH or
                      DateUtils.FORMAT_SHOW_WEEKDAY or
                      DateUtils.FORMAT_ABBREV_WEEKDAY
            } else {
                DateUtils.FORMAT_SHOW_YEAR or
                      DateUtils.FORMAT_ABBREV_MONTH or
                      DateUtils.FORMAT_NO_MONTH_DAY
            }


            DateUtils.formatDateTime(context, this, flags)
        }
    }
}

/**
 * Formats this long (interpreted as a timestamp) relative to the current time. Resolves to relative dates only for
 * "Yesterday", "Today" and "Tomorrow". If instead the timestamp is further away in the future it will resolve to the
 * weekday, e.g. "Sunday".
 */
fun Long.toRelativeWeekday(): String {
    val midnight = Calendar.getInstance()
    midnight.set(Calendar.HOUR_OF_DAY, 0)
    midnight.set(Calendar.MINUTE, 0)
    midnight.set(Calendar.SECOND, 0)
    midnight.set(Calendar.MILLISECOND, 0)

    val midnightYesterday = midnight.timeInMillis - DateUtils.DAY_IN_MILLIS
    val midnightInTwoDays = midnight.timeInMillis + 2 * DateUtils.DAY_IN_MILLIS

    return when {
        this in midnightYesterday..(midnightInTwoDays - 1) -> {
            DateUtils.getRelativeTimeSpanString(
                  this, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS, 0
            ).toString()
        }

        else -> {
            SimpleDateFormat("EEEE", Locale.getDefault()).format(this)
        }
    }
}

fun getDateFormatUTC(format: String): SimpleDateFormat {
    val simpleDateFormat = SimpleDateFormat(format, Locale.US)
    simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return simpleDateFormat
}

fun Date.toShortFormatString(): String = getDateFormatUTC("dd MMM yyyy").format(this)
fun Date.toStringWithFormat(format: String): String = getDateFormatUTC(format).format(this)

fun mergeDateAndTime(date: Date, time: Date): Date {

    val calendar = Calendar.getInstance()
    calendar.time = date

    val aTime = Calendar.getInstance()
    aTime.time = time

    val dateTime = Calendar.getInstance()
    dateTime.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
    dateTime.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
    dateTime.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    dateTime.set(Calendar.HOUR_OF_DAY, aTime.get(Calendar.HOUR_OF_DAY))
    dateTime.set(Calendar.MINUTE, aTime.get(Calendar.MINUTE))
    dateTime.set(Calendar.SECOND, aTime.get(Calendar.SECOND))

    return dateTime.time
}

fun Date.addSeconds(seconds: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this

    calendar.add(Calendar.SECOND, seconds)

    return calendar.time
}

fun Date.addDays(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this

    calendar.add(Calendar.DAY_OF_YEAR, days)

    return calendar.time
}

fun Date.isInPast(): Boolean {
    return this.before(Date())
}

fun String.toDateWithFormat(format: String): Date {
    return SimpleDateFormat(format, Locale.getDefault()).parse(this)
}