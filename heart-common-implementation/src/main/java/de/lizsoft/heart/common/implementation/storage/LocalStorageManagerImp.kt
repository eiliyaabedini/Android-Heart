package de.lizsoft.heart.common.implementation.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import de.lizsoft.heart.interfaces.common.LocalStorageManager
import de.lizsoft.heart.interfaces.common.model.HistoricalData
import de.lizsoft.heart.interfaces.common.model.HistoricalSavedData

class LocalStorageManagerImp(
      private val preferences: SharedPreferences,
      private val gson: Gson
) : LocalStorageManager {

    override fun getStringByKey(key: String, defaultValue: String?): String? {
        return preferences.getString(key, defaultValue)
    }

    override fun saveStringByKey(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    override fun getIntByKey(key: String, defaultValue: Int): Int {
        return preferences.getInt(key, defaultValue)
    }

    override fun saveIntByKey(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    override fun <T> getByKey(key: String, clazz: Class<T>): T? {
        val userJson = preferences.getString(key, null)
        return userJson?.let { gson.fromJson(it, clazz) }
    }

    override fun <T> saveByKey(key: String, model: T) {
        preferences.edit().putString(key, gson.toJson(model)).apply()
    }

    override fun getBoolean(key: String, default: Boolean): Boolean {
        return preferences.getBoolean(key, default)
    }

    override fun setBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    override fun <T> getListByKey(key: String, clazz: Class<T>): List<T>? {
        return preferences.getStringSet(key, emptySet())
              ?.toList()
              ?.map { model -> gson.fromJson(model, clazz) }
    }

    override fun <T> pushToListByKey(key: String, model: T, clazz: Class<T>) {
        val set = HashSet<String>()
        getListByKey(key, clazz)?.let { set.addAll(it.map { oldModel -> gson.toJson(oldModel) }) }
        set.add(gson.toJson(model))
        preferences.edit().putStringSet(key, set).apply()
    }

    override fun <T> setListsByKey(key: String, models: List<T>, clazz: Class<T>) {
        val set = HashSet<String>()
        set.addAll(models.map { model -> gson.toJson(model) })
        preferences.edit().putStringSet(key, set).apply()
    }

    override fun <T> removeFromListByKey(key: String, model: T, clazz: Class<T>) {
        val set = HashSet<String>()
        getListByKey(key, clazz)?.let {
            set.addAll(it
                  .filter { it != model }
                  .map { oldModel -> gson.toJson(oldModel) })
        }
        preferences.edit().putStringSet(key, set).apply()
    }

    override fun <T> getHistoricalListByKey(key: String, clazz: Class<T>): List<HistoricalData<T>>? {
        return getListByKey(key, HistoricalSavedData::class.java)?.map { model ->
            HistoricalData<T>(
                  model = gson.fromJson(model.modelString, clazz),
                  timestamp = model.timestamp
            )
        }?.sortedBy { it.timestamp }
    }

    override fun <T> pushToHistoricalListByKey(
          key: String,
          model: T,
          clazz: Class<T>,
          pushIfChanged: Boolean,
          recordsLimitation: Int
    ) {
        val items: List<HistoricalData<T>> = getHistoricalListByKey(key, clazz) ?: emptyList()

        val newItems: MutableList<HistoricalSavedData> = items.map {
            HistoricalSavedData(
                  modelString = gson.toJson(it.model),
                  timestamp = it.timestamp
            )
        }
              .sortedBy { it.timestamp }
              .toMutableList()

        if (items.isNotEmpty() && pushIfChanged) {
            if (items.last().model != model) {
                newItems.add(HistoricalSavedData(gson.toJson(model)))
            }
        } else {
            newItems.add(HistoricalSavedData(gson.toJson(model)))
        }

        val set = HashSet<String>()
        set.addAll(
              newItems
                    .sortedBy { it.timestamp }
                    .takeLast(if (recordsLimitation > 1) recordsLimitation else 100)
                    .map { gson.toJson(it) }
        )
        preferences.edit().putStringSet(key, set).apply()
    }

    override fun removeByKey(key: String) {
        preferences.edit().remove(key).apply()
    }
}