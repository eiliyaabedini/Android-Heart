package de.lizsoft.heart.common.extension

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import de.lizsoft.heart.interfaces.ResponseResult
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber
import java.util.*
import java.util.regex.Pattern

fun Calendar.fromDate(date: Date): Calendar {
    time = date
    return this
}

fun logError(errorMessage: String) {
    Timber.e(errorMessage)
}

fun logError(t: Throwable) {
    Timber.e(t)
}

fun logNonFatal(message: String, t: Throwable?) {
    t?.let { Timber.w(it, message) }

    if (t == null) {
        Timber.w(message)
    }

    Handler(Looper.getMainLooper()).post {
        //            val context by inject<Context>(Qualifiers.applicationContext)
        //            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

private val EMAIL_ADDRESS = Pattern.compile(
      "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
)

fun isEmailValid(email: String): Boolean {
    return EMAIL_ADDRESS.matcher(email).matches()
}

fun Activity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun dp2px(dp: Int): Float {
    return (dp * Resources.getSystem().displayMetrics.density)
}

fun <T : Any> T.toResponseResult() = ResponseResult.Success(this)

fun <T : Any> T.ToResponseResultMaybe() = Maybe.just<ResponseResult<T>>(this.toResponseResult())
fun <T : Any> T.ToResponseResultSingle() = Single.just<ResponseResult<T>>(this.toResponseResult())