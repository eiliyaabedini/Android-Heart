package de.lizsoft.heart.interfaces.common

import de.lizsoft.heart.interfaces.common.model.HistoricalData

interface LocalStorageManager {

    fun getStringByKey(key: String, defaultValue: String?): String?
    fun saveStringByKey(key: String, value: String)

    fun getIntByKey(key: String, defaultValue: Int): Int
    fun saveIntByKey(key: String, value: Int)

    fun <T> getByKey(key: String, clazz: Class<T>): T?
    fun <T> saveByKey(key: String, model: T)

    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun setBoolean(key: String, value: Boolean)

    fun <T> getListByKey(key: String, clazz: Class<T>): List<T>?
    fun <T> pushToListByKey(key: String, model: T, clazz: Class<T>)
    fun <T> setListsByKey(key: String, models: List<T>, clazz: Class<T>)
    fun <T> removeFromListByKey(key: String, model: T, clazz: Class<T>)

    fun <T> getHistoricalListByKey(key: String, clazz: Class<T>): List<HistoricalData<T>>?
    fun <T> pushToHistoricalListByKey(
        key: String,
        model: T,
        clazz: Class<T>,
        pushIfChanged: Boolean = true,
        recordsLimitation: Int
    )

    fun removeByKey(key: String)
}