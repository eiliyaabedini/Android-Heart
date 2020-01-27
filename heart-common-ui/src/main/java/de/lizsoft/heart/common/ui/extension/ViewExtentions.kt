package de.lizsoft.heart.common.ui.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jakewharton.rxbinding3.view.globalLayouts
import com.tapadoo.alerter.Alerter
import de.lizsoft.heart.common.extension.dp2px
import de.lizsoft.heart.common.ui.ActivityWithPresenter
import de.lizsoft.heart.common.ui.R
import de.lizsoft.heart.common.ui.glide.CropCircleWithBorderTransformation
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import de.lizsoft.heart.interfaces.pushnotification.InternalAlerter
import de.lizsoft.heart.interfaces.pushnotification.model.AlerterColorType
import de.lizsoft.heart.interfaces.pushnotification.model.NotificationType
import io.reactivex.Maybe
import timber.log.Timber

fun Context.getBitmapFromVectorDrawable(@DrawableRes drawableId: Int): Bitmap {
    val drawable = AppCompatResources.getDrawable(this, drawableId)!!

    val bitmap = Bitmap.createBitmap(
          drawable.intrinsicWidth,
          drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}

fun View.getDistanceFromBottom(): Int {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)

    val onScreenPosition = IntArray(2)
    getLocationOnScreen(onScreenPosition)

    return metrics.heightPixels - onScreenPosition[1]
}

fun DialogFragment.showAllowingStateLoss(manager: FragmentManager, tag: String) {
    manager.beginTransaction().add(this, tag).commitAllowingStateLoss()
}

@RequiresApi(Build.VERSION_CODES.M)
fun Window.setLightStatusBar() {
    var flags = this.decorView.systemUiVisibility
    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    this.decorView.systemUiVisibility = flags
}

fun View.laidOut(): Maybe<Unit> {
    return if (isLaidOut) {
        Maybe.just(Unit)
    } else {
        globalLayouts().filter { isLaidOut }.firstElement()
    }
}

fun TextView.setTextOrGone(text: String?) {
    if (text.isNullOrEmpty()) {
        this.isGone = true
    } else {
        this.text = text
        this.isGone = false
    }
}

fun ImageView.showAvatar(avatarUrl: String?, placeHolder: Int = R.drawable.default_avatar_placeholder) {
    Glide.with(context)
          .load(avatarUrl)
          .apply(RequestOptions.circleCropTransform().error(placeHolder))
          .into(this)
}

fun View.setMarginBottom(marginDp: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = dp2px(marginDp).toInt()
}

fun Context.loadAvatarWithBorder(
      userAvatar: String?,
      @ColorInt borderColor: Int = Color.WHITE,
      callback: (Bitmap) -> Unit
) {
    Glide.with(this)
          .asBitmap()
          .apply {
              if (userAvatar != null) {
                  load(userAvatar)
              } else {
                  load(R.drawable.default_avatar_placeholder)
              }
          }
          .apply(
                RequestOptions()
                      .error(R.drawable.default_avatar_placeholder)
                      .override(dp2px(32).toInt())
                      .centerCrop()
                      .transform(CropCircleWithBorderTransformation(dp2px(4), borderColor))
          )
          .listener(object : RequestListener<Bitmap> {
              override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
              ): Boolean {
                  e?.printStackTrace()
                  return false
              }

              override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
              ): Boolean {
                  return if (resource != null) {
                      callback(resource)
                      true
                  } else {
                      false
                  }
              }
          })
          .submit()
}

fun BottomSheetBehavior<*>.disableDragging() {
    isHideable = false

    setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }
    })
}

fun RecyclerView.disablePullToRefreshWhenScrollingHorizontally(listRefreshLayout: SwipeRefreshLayout) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            listRefreshLayout.isEnabled = dx == 0
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                listRefreshLayout.isEnabled = true
            }
        }
    })
}

fun View.addRippleEffect() {
    val outValue = TypedValue()
    context!!.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
    setBackgroundResource(outValue.resourceId)
}

fun ActivityWithPresenterInterface.showInternalAlertForNotifications(
      notificationType: NotificationType,
      clickCallback: (ActivityWithPresenterInterface, InternalAlerter) -> Unit
) {
    showInternalAlert(
          title = notificationType.notificationTitle,
          message = notificationType.notificationBody,
          alerterColorType = notificationType.alerterColorType,
          notificationIconRes = notificationType.notificationIconRes,
          clickCallback = clickCallback
    )
}

fun ActivityWithPresenterInterface.showInternalAlert(
      title: String?,
      message: String,
      alerterColorType: AlerterColorType,
      @DrawableRes notificationIconRes: Int?,
      clickCallback: (ActivityWithPresenterInterface, InternalAlerter) -> Unit
) {
    val alerter: Alerter = Alerter.create(this as AppCompatActivity)
    title?.let { alerter.setTitle(it) }
    alerter.setText(message)

    alerter.setBackgroundColorRes(
          getColorFromNotificationColorType(alerterColorType)
    )
    alerter.setDuration(3000)

    notificationIconRes?.let { alerter.setIcon(it) } ?: alerter.hideIcon()

    alerter.setOnClickListener(View.OnClickListener {
        Timber.d("alerter clicked activity.scope:${this.getCurrentScreenBucketModel().scope}")

        clickCallback(this, object : InternalAlerter {
            override fun hide() {
                Alerter.hide()
            }
        })
    })

    alerter.show()
}

private fun getColorFromNotificationColorType(alerterColorType: AlerterColorType): Int {
    return when (alerterColorType) {
        AlerterColorType.SUCCESS -> R.color.success
        AlerterColorType.ERROR -> R.color.error
        AlerterColorType.BLACK -> R.color.black
    }
}

fun <V : View> ActivityWithPresenter.findView(id: Int): Lazy<V> = lazy {
    findViewById<V>(id)
}

fun <V : View> ActivityWithPresenter.bindView(id: Int): Lazy<V> = lazy {
    findViewByIdFromContent<V>(id)
}

fun <V : View> View.bindView(id: Int): Lazy<V> = lazy {
    findViewById<V>(id)
}

fun <V : View> Fragment.bindView(id: Int): Lazy<V> = lazy {
    view!!.findViewById<V>(id)
}
