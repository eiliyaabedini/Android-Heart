package de.lizsoft.heart.interfaces.common.rx

import com.google.android.gms.tasks.Task
import de.lizsoft.heart.interfaces.ResponseResult
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.subjects.MaybeSubject
import io.reactivex.subjects.SingleSubject
import timber.log.Timber
import java.util.concurrent.CancellationException

fun <T> Task<T>.toMaybeResult(): Maybe<ResponseResult<T>> {
    val subject = MaybeSubject.create<ResponseResult<T>>()
    addOnSuccessListener {
        Timber.d("Task addOnSuccessListener $it")
        subject.onSuccess(ResponseResult.Success<T>(it))
    }

    addOnCanceledListener {
        Timber.d("Task addOnCanceledListener")
        subject.onComplete()
    }

    addOnCompleteListener {
        Timber.d("Task addOnCompleteListener")
        subject.onComplete()
    }

    addOnFailureListener {
        it.printStackTrace()
        Timber.d("Task addOnFailureListener")
        subject.onSuccess(ResponseResult.Failure(it))
    }

    return subject
}

fun <T> Task<T>.toSingleTaskResult(): Single<ResponseResult<Task<T>>> {
    val subject = SingleSubject.create<ResponseResult<Task<T>>>()
    addOnCanceledListener {
        Timber.d("Task addOnCanceledListener")
        subject.onSuccess(ResponseResult.Failure(CancellationException()))
    }

    addOnCompleteListener { task: Task<T> ->
        Timber.d("Task addOnCompleteListener")
        subject.onSuccess(ResponseResult.Success<Task<T>>(task))
    }

    addOnFailureListener {
        it.printStackTrace()
        Timber.d("Task addOnFailureListener")
        subject.onSuccess(ResponseResult.Failure(it))
    }

    return subject
}

fun <T : Any> T.ToMaybe() = Maybe.just<T>(this)
fun <T : Any> T.ToSingle() = Single.just<T>(this)