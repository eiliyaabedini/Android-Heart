package de.lizsoft.heart.common.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.core.scrollChangeEvents
import de.lizsoft.heart.common.extension.dp2px
import de.lizsoft.heart.common.extension.hideKeyboard
import de.lizsoft.heart.common.extension.mergeDateAndTime
import de.lizsoft.heart.common.ui.factory.DialogFactory
import de.lizsoft.heart.common.ui.ui.bottomsheet.TextBottomSheet
import de.lizsoft.heart.common.ui.ui.bottomsheet.model.TextBottomSheetModel
import de.lizsoft.heart.interfaces.common.*
import de.lizsoft.heart.interfaces.common.presenter.PresenterAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonAction
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonView
import de.lizsoft.heart.interfaces.common.ui.ActivityResult
import de.lizsoft.heart.interfaces.common.ui.ActivityWithPresenterInterface
import de.lizsoft.heart.interfaces.common.ui.ScreenBucketModel
import de.lizsoft.heart.interfaces.dialog.DialogActivityModel
import de.lizsoft.heart.viewstatemanager.StateManager
import de.lizsoft.heart.viewstatemanager.ViewState
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.bindScope
import org.koin.core.scope.Scope
import timber.log.Timber
import java.io.Serializable
import java.sql.Time
import java.util.*

abstract class ActivityWithPresenter : AppCompatActivity(),
      ActivityWithPresenterInterface {

    protected val disposables = CompositeDisposable()

    protected val textUtils: TextUtils by inject()
    protected val dateUtils: DateUtils by inject()
    protected val drawableUtils: DrawableUtils by inject()
    protected val dialogFactory: DialogFactory by inject()
    protected val navigator: Navigator by inject()
    protected val reactiveTransformer: ReactiveTransformer by inject()
    private var activityResults: BehaviorSubject<ActivityResult> = BehaviorSubject.create()
    protected val actions: BehaviorSubject<PresenterAction> = BehaviorSubject.create()

    private lateinit var screenBucket: ScreenBucket<*>
    private lateinit var stateManager: StateManager<PresenterAction>

    private var tag: String? = null

    private val presenterCommonView = object : PresenterCommonView {

        override fun actions(): Observable<PresenterAction> = actions
              .onErrorReturnItem(PresenterCommonAction.ErrorRetryClicked)

        override fun getActivity(): ActivityWithPresenterInterface {
            return navigator.getActivity()
        }

        override fun getCurrentScope(): Scope = getCurrentScreenBucketModel().scope

        override fun passActionInternal(action: PresenterAction) {
            actions.onNext(action)
        }

        override fun showContent() {
            runOnUiThread {
                stateManager.changeState(ViewState.Normal)
            }
        }

        override fun showContentLoading() {
            runOnUiThread {
                hideKeyboard()
                if (getPullToRefreshLayout()?.isRefreshing == true) {
                    stateManager.changeState(ViewState.Normal)
                } else {
                    stateManager.changeState(ViewState.Loading)
                }
            }
        }

        override fun showContentEmpty() {
            runOnUiThread {
                hideKeyboard()
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        override fun showContentError() {
            runOnUiThread {
                hideKeyboard()
                stateManager.changeState(ViewState.Error)
            }
        }

        override fun makeItemsDialog(
              title: String,
              vararg items: Pair<String, () -> Unit>,
              finally: () -> Unit
        ) {
            runOnUiThread {
                dialogFactory.makeItemsDialog(
                      context = this@ActivityWithPresenter,
                      title = title,
                      items = *items,
                      finally = finally
                )
            }
        }

        override fun makeMultiSelectItemsDialog(
              title: String,
              vararg items: Pair<String, Boolean>,
              finally: (List<String>) -> Unit
        ) {
            runOnUiThread {
                dialogFactory.makeMultiSelectItemsDialog(
                      context = this@ActivityWithPresenter,
                      title = title,
                      items = *items,
                      finally = finally
                )
            }
        }

        override fun makeRadioItemsDialog(vararg items: Pair<String, () -> Unit>) {
            runOnUiThread {
                dialogFactory.makeRadioItemsDialog(this@ActivityWithPresenter, *items)
            }
        }

        override fun showNormalDialog(messageId: Int) {
            runOnUiThread {
                dialogFactory.showNormalDialog(this@ActivityWithPresenter, messageId)
            }
        }

        override fun showDialogDatePicker(defaultDate: Date?, isPossibleToSelectPastDates: Boolean): Maybe<Date> {
            return dialogFactory.showDatePickerDialog(
                  context = this@ActivityWithPresenter,
                  defaultCalendar = Calendar.getInstance().apply {
                      time = defaultDate ?: Calendar.getInstance().time
                  },
                  reactiveTransformer = reactiveTransformer
            )
        }

        override fun showDialogTimePicker(defaultTime: Date): Maybe<Time> {
            return dialogFactory.showTimePickerDialog(
                  context = this@ActivityWithPresenter,
                  defaultCalendar = Calendar.getInstance().apply { time = defaultTime },
                  reactiveTransformer = reactiveTransformer
            )
        }

        override fun showDialogDateTimePicker(): Maybe<Date> {
            return dialogFactory.showDatePickerDialog(
                  context = this@ActivityWithPresenter,
                  reactiveTransformer = reactiveTransformer
            )
                  .flatMap { date ->
                      dialogFactory.showTimePickerDialog(
                            context = this@ActivityWithPresenter,
                            reactiveTransformer = reactiveTransformer
                      )
                            .map { time ->
                                return@map mergeDateAndTime(date, time)
                            }
                  }
        }

        override fun showSnackBar(message: String) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                  .show()
        }

        override fun callPhoneNumber(phoneNumber: String) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }

        override fun sendSmsToPhoneNumber(phoneNumber: String) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:$phoneNumber")
            //        intent.putExtra("sms_body", "The SMS text")
            startActivity(intent)
        }

        override fun showCallAndSmsBottomSheet(phoneNumber: String) {
            TextBottomSheet.showTexts(
                  supportFragmentManager = supportFragmentManager,
                  texts = listOf(
                        TextBottomSheetModel(
                              text = textUtils.getString(R.string.common_send_sms),
                              clickCallback = { delegate ->
                                  delegate.dismiss()
                                  sendSmsToPhoneNumber(phoneNumber)
                              }
                        ),
                        TextBottomSheetModel(
                              text = textUtils.getString(R.string.common_call),
                              clickCallback = { delegate ->
                                  delegate.dismiss()
                                  callPhoneNumber(phoneNumber)
                              }
                        )
                  )
            )
        }

        override fun closeScreenWithResult(serializable: Serializable) {
            runOnUiThread {
                setResult(0, Intent().apply { putExtra(ActivityWithPresenterInterface.RESULT_INTENT_KEY, serializable) })
                closeScreen()
            }
        }

        override fun closeScreen() {
            runOnUiThread {
                finish()
            }
        }

        override fun restartAPP() {
            runOnUiThread {
                navigator.restartAPP()
            }
        }

        override fun openUrl(url: String) {
            navigator.openUrl(url)
        }

        override fun openDialogScreen(model: DialogActivityModel) {
            navigator.openDialogScreen(model)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        screenBucket = getCurrentScreenBucket()
        Timber.d("onCreate called for screenBucket:${screenBucket.screenBucketModel}")

        setContentView(R.layout.activity_with_presenter_layout)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        stateManager = StateManager(
              context = baseContext,
              contentLayout = findViewById(R.id.activity_with_presenter_content),
              overlayLayout = findViewById(R.id.activity_with_presenter_overlay),
              normalLayout = getCurrentScreenBucketModel().viewLayout,
              loadingDefaultLayout = R.layout.state_manager_default_loading,
              loadingLayoutId = getCurrentScreenBucketModel().loadingLayout,
              errorDefaultLayout = R.layout.state_manager_default_error,
              errorLayoutId = getCurrentScreenBucketModel().errorLayout,
              errorRetryButtonDefaultLayout = R.id.state_manager_default_error_retry_button,
              errorRetryButtonId = getCurrentScreenBucketModel().errorRetryButtonId,
              onErrorRetryCallback = { actions.onNext(PresenterCommonAction.ErrorRetryClicked) }
        )
        stateManager.changeState(ViewState.Normal)

        bindScope(getCurrentScreenBucketModel().scope)

        navigator.setActivity(this)

        drawActionBarAndSystemBar()

        initializeBeforePresenter()

        screenBucket.attachView(presenterCommonView)

        initializeViewListeners()

        screenBucket.initialise()
    }

    override fun onDestroy() {
        Timber.d("onDestroy called for screenBucket:${screenBucket.screenBucketModel}")

        disposables.clear()
        screenBucket.detachView()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        if (getCurrentScreenBucketModel().hideSystemBar) {
            hideSystemBar()
        }

        navigator.setActivity(this)
        screenBucket.viewIsVisible()
    }

    override fun onPause() {
        super.onPause()

        screenBucket.viewIsHidden()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun drawActionBarAndSystemBar() {
        getViewToolbar()?.let { setSupportActionBar(it) }
        if (getCurrentScreenBucketModel().hideSystemBar) {
            hideSystemBar()
        } else if (getCurrentScreenBucketModel().enableDisplayHomeAsUp) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        supportActionBar?.elevation = 0f
    }

    protected fun hideSystemBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
    }

    protected fun enableSmartElevationForActionBar(scrollView: NestedScrollView) {
        disposables += scrollView.scrollChangeEvents()
              .map { it.scrollY }
              .startWith(scrollView.scrollY)
              .subscribe { scrollY ->
                  updateActionBarElevationWithCurrentScroll(scrollY)
              }
    }

    protected fun enableSmartElevationForActionBar(recyclerView: RecyclerView) {
        updateActionBarElevationWithCurrentScroll(0)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var totalScrolled: Int = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                totalScrolled += dy

                updateActionBarElevationWithCurrentScroll(totalScrolled)
            }
        })
    }

    private fun updateActionBarElevationWithCurrentScroll(scrollY: Int) {
        if (scrollY == 0) {
            supportActionBar?.elevation = 0f
        } else {
            supportActionBar?.elevation = dp2px(4)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        activityResults.onNext(ActivityResult(requestCode, data))
    }

    abstract fun getCurrentScreenBucket(): ScreenBucket<*>

    open fun getViewToolbar(): Toolbar? = null

    open fun getPullToRefreshLayout(): SwipeRefreshLayout? = null

    override fun getCurrentScreenBucketModel(): ScreenBucketModel = screenBucket.screenBucketModel

    override fun setActivityTag(activityTag: String?) {
        this.tag = activityTag
    }

    override fun getActivityTag(): String? = this.tag

    override fun getCommonView(): PresenterCommonView = presenterCommonView

    override fun getContext(): Context = this

    override fun <V : View> findViewByIdFromContent(@IdRes id: Int): V = findViewById<ViewGroup>(R.id.activity_with_presenter_content).findViewById(id)

    override fun <V : View> findViewByIdFromOverlay(@IdRes id: Int): V = findViewById<ViewGroup>(R.id.activity_with_presenter_overlay).findViewById(id)

    override fun startActivityFromParent(intent: Intent, options: Bundle?) {
        startActivity(intent, options)
    }

    override fun startActivityForResultFromParent(intent: Intent, requestCode: Int, options: Bundle?) {
        startActivityForResult(intent, requestCode, options)

    }

    override fun observeActivityResult(): Observable<ActivityResult> = activityResults
          .filter { it != ActivityResult.EMPTY }
          .doOnNext { activityResults.onNext(ActivityResult.EMPTY) }


    override fun finishActivity() {
        finish()
    }
}
