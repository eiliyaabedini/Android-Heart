package de.lizsoft.heart.common.ui.extension

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.view.View
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <V : View> View.bindView(id: Int)
      : ReadOnlyProperty<View, V> = required(id, viewFinder)

fun <V : View> Activity.bindView(id: Int)
      : ReadOnlyProperty<Activity, V> = required(id, viewFinder)

fun <V : View> Fragment.bindView(id: Int)
      : ReadOnlyProperty<Fragment, V> = required(id, viewFinder)

fun <V : View> Dialog.bindView(id: Int)
      : ReadOnlyProperty<Dialog, V> = required(id, viewFinder)

fun <V : View> RecyclerView.ViewHolder.bindView(id: Int)
      : ReadOnlyProperty<RecyclerView.ViewHolder, V> = required(id, viewFinder)

fun <V : View> View.bindOptionalView(id: Int)
      : ReadOnlyProperty<View, V?> = optional(id, viewFinder)

fun <V : View> Activity.bindOptionalView(id: Int)
      : ReadOnlyProperty<Activity, V?> = optional(id, viewFinder)

fun <V : View> Dialog.bindOptionalView(id: Int)
      : ReadOnlyProperty<Dialog, V?> = optional(id, viewFinder)

fun <V : View> RecyclerView.ViewHolder.bindOptionalView(id: Int)
      : ReadOnlyProperty<RecyclerView.ViewHolder, V?> = optional(id, viewFinder)

fun <V : View> View.bindViews(vararg ids: Int)
      : ReadOnlyProperty<View, List<V>> = required(ids, viewFinder)

fun <V : View> Activity.bindViews(vararg ids: Int)
      : ReadOnlyProperty<Activity, List<V>> = required(ids, viewFinder)

fun <V : View> Dialog.bindViews(vararg ids: Int)
      : ReadOnlyProperty<Dialog, List<V>> = required(ids, viewFinder)

fun <V : View> RecyclerView.ViewHolder.bindViews(vararg ids: Int)
      : ReadOnlyProperty<RecyclerView.ViewHolder, List<V>> = required(ids, viewFinder)

fun <V : View> View.bindOptionalViews(vararg ids: Int)
      : ReadOnlyProperty<View, List<V>> = optional(ids, viewFinder)

fun <V : View> Activity.bindOptionalViews(vararg ids: Int)
      : ReadOnlyProperty<Activity, List<V>> = optional(ids, viewFinder)

fun <V : View> Dialog.bindOptionalViews(vararg ids: Int)
      : ReadOnlyProperty<Dialog, List<V>> = optional(ids, viewFinder)

fun <V : View> RecyclerView.ViewHolder.bindOptionalViews(vararg ids: Int)
      : ReadOnlyProperty<RecyclerView.ViewHolder, List<V>> = optional(ids, viewFinder)

fun View.bindDimen(@DimenRes id: Int)
      : ReadOnlyProperty<View, Float> = requiredDimen(id, dimenFinder)

fun Activity.bindDimen(@DimenRes id: Int)
      : ReadOnlyProperty<Activity, Float> = requiredDimen(id, dimenFinder)

fun Dialog.bindDimen(@DimenRes id: Int)
      : ReadOnlyProperty<Dialog, Float> = requiredDimen(id, dimenFinder)

fun RecyclerView.ViewHolder.bindDimen(@DimenRes id: Int)
      : ReadOnlyProperty<RecyclerView.ViewHolder, Float> = requiredDimen(id, dimenFinder)

private val View.viewFinder: View.(Int) -> View?
    get() = { findViewById(it) }
private val Activity.viewFinder: Activity.(Int) -> View?
    get() = { findViewById(it) }
private val Fragment.viewFinder: Fragment.(Int) -> View?
    get() = { view?.findViewById(it) }
private val Dialog.viewFinder: Dialog.(Int) -> View?
    get() = { findViewById(it) }
private val RecyclerView.ViewHolder.viewFinder: RecyclerView.ViewHolder.(Int) -> View?
    get() = { itemView.findViewById(it) }

private val View.dimenFinder: View.(Int) -> Float?
    get() = { resources.getDimension(it) }
private val Activity.dimenFinder: Activity.(Int) -> Float?
    get() = { resources.getDimension(it) }
private val Dialog.dimenFinder: Dialog.(Int) -> Float?
    get() = { context.resources.getDimension(it) }
private val RecyclerView.ViewHolder.dimenFinder: RecyclerView.ViewHolder.(Int) -> Float?
    get() = { itemView.resources.getDimension(it) }

private fun viewNotFound(id: Int, desc: KProperty<*>): Nothing =
      throw IllegalStateException("View ID $id for '${desc.name}' not found.")

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> required(id: Int, finder: T.(Int) -> View?) = Lazy { t: T, desc ->
    t.finder(id) as V? ?: viewNotFound(id, desc)
}

@Suppress("UNCHECKED_CAST")
private fun <T> requiredDimen(id: Int, finder: T.(Int) -> Float?) = Lazy { t: T, _ ->
    t.finder(id) ?: -1f
}

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> optional(id: Int, finder: T.(Int) -> View?) =
      Lazy { t: T, _ -> t.finder(id) as V? }

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> required(ids: IntArray, finder: T.(Int) -> View?) =
      Lazy { t: T, desc ->
          ids.map {
              t.finder(it) as V? ?: viewNotFound(it, desc)
          }
      }

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> optional(ids: IntArray, finder: T.(Int) -> View?) =
      Lazy { t: T, _ ->
          ids.map {
              t.finder(it) as V?
          }.filterNotNull()
      }

// Like Kotlin's lazy delegate but the initializer gets the target and metadata passed to it
private class Lazy<in T, out V>(private val initializer: (T, KProperty<*>) -> V) : ReadOnlyProperty<T, V> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        LazyRegistry.register(thisRef!!, this)

        if (value == EMPTY) {
            value = initializer(thisRef, property)
        }

        @Suppress("UNCHECKED_CAST")
        return value as V
    }

    fun reset() {
        value = EMPTY
    }
}

object KotterKnife {
    fun reset(target: Any) = LazyRegistry.reset(target)
}

private object LazyRegistry {
    private val lazyMap = WeakHashMap<Any, MutableCollection<Lazy<*, *>>>()

    fun register(target: Any, lazy: Lazy<*, *>) {
        lazyMap.getOrPut(target, { Collections.newSetFromMap(WeakHashMap()) }).add(lazy)
    }

    fun reset(target: Any) {
        lazyMap[target]?.forEach { it.reset() }
    }
}

fun <T : Application> Activity.bindParent(): ReadOnlyProperty<Activity, T> =
      ApplicationCaster<T>()

private class ApplicationCaster<out T> : ReadOnlyProperty<Activity, T> where T : Application {

    override fun getValue(thisRef: Activity, property: KProperty<*>): T {
        @Suppress("UNCHECKED_CAST")
        return thisRef.application as T
    }
}