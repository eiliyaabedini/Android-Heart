package de.lizsoft.heart.common.ui.imagepicker

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.content.Context
import androidx.core.content.ContextCompat
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.yalantis.ucrop.UCrop
import de.lizsoft.heart.common.ui.R
import de.lizsoft.heart.common.ui.factory.DialogFactory
import de.lizsoft.heart.interfaces.ResponseResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.io.File

//https://github.com/MLSDev/RxImagePicker
class ImagePickerHandler {

    private val publishTypes = PublishSubject.create<ImagePickerType>()

    fun showItemsDialog(context: Context, dialogFactory: DialogFactory) {
        dialogFactory.makeItemsDialog(
              context,
              "Select",
              "Gallery" to {
                  publishTypes.onNext(ImagePickerType.Gallery)
              },
              "Camera" to {
                  publishTypes.onNext(ImagePickerType.Camera)
              }
        )
    }

    fun observe(activity: Activity): Observable<ResponseResult<File>> {

        val options = UCrop.Options()
        options.withAspectRatio(1.0f, 1.0f)
        options.setToolbarColor(ContextCompat.getColor(activity, R.color.primary))
        options.setStatusBarColor(ContextCompat.getColor(activity, R.color.primary))

        return publishTypes
              .switchMap { source ->
                  RxPaparazzo.single(activity)
                        .crop(options)
                        .let {
                            when (source) {
                                ImagePickerType.Gallery -> {
                                    it.usingGallery()
                                }

                                ImagePickerType.Camera -> {
                                    it.usingCamera()
                                }
                            }
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map { response ->
                            Timber.d("RxPaparazzo received resultCode:${response.resultCode()}")
                            if (response.resultCode() == RESULT_OK && response.data() != null) {
                                Timber.d("RxPaparazzo received Success")
                                ResponseResult.Success<File>(response.data().file)
                            } else {
                                Timber.d("RxPaparazzo received Failure")
                                ResponseResult.Failure<File>(PendingIntent.CanceledException())
                            }
                        }
                        .doOnError { it.printStackTrace() }
              }
    }
}