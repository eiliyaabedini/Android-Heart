package de.lizsoft.heart.interfaces.common.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import de.lizsoft.heart.interfaces.common.presenter.PresenterCommonView
import io.reactivex.Observable

interface ActivityWithPresenterInterface {

    fun getCurrentScreenBucketModel(): ScreenBucketModel

    //Retrieve params that presenter is going to use immediately
    fun initializeBeforePresenter() {

    }

    //Add View clicks here
    //Make sure to reinit whole views here
    fun initializeViewListeners() {

    }

    fun setActivityTag(activityTag: String?)
    fun getActivityTag(): String?

    fun getCommonView(): PresenterCommonView
    fun getContext(): Context

    fun <V : View> findViewByIdFromContent(id: Int): V
    fun <V : View> findViewByIdFromOverlay(id: Int): V

    fun startActivityFromParent(intent: Intent, options: Bundle? = null)
    fun startActivityForResultFromParent(intent: Intent, requestCode: Int, options: Bundle? = null)
    fun observeActivityResult(): Observable<ActivityResult>
    fun finishActivity()

    companion object {
        const val RESULT_INTENT_KEY = "de.lizsoft.heart.common.ui.ActivityWithPresenter.RESULT_INTENT_KEY"
    }
}
