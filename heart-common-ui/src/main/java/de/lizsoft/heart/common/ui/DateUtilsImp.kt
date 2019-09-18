package de.lizsoft.heart.common.ui

import android.content.Context
import de.lizsoft.heart.common.extension.toTimeString
import de.lizsoft.heart.interfaces.common.DateUtils
import java.util.*

class DateUtilsImp(private val context: Context) : DateUtils {

    override fun dateToTimeString(date: Date?): String = date?.time?.toTimeString(context) ?: ""
}