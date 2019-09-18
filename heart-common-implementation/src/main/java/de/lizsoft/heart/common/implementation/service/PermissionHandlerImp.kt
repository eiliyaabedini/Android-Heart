package de.lizsoft.heart.common.implementation.service

import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import de.lizsoft.heart.common.implementation.lifecycle.LifeCycleObject
import de.lizsoft.heart.interfaces.common.PermissionHandler
import de.lizsoft.heart.interfaces.common.ReactiveTransformer
import io.reactivex.Maybe
import org.koin.core.scope.Scope

class PermissionHandlerImp(
    private val reactiveTransformer: ReactiveTransformer
) : PermissionHandler() {

    //Manifest.permission.ACCESS_FINE_LOCATION
    override fun getPermission(scope: Scope, permission: String): Maybe<Boolean> {
        return LifeCycleObject(scope).getScopedActivity()
              .flatMap { activity ->
                  RxPermissions(activity as FragmentActivity)
                        .request(permission)
                        .subscribeOn(reactiveTransformer.mainThreadScheduler())
                        .firstElement()
              }
              .observeOn(reactiveTransformer.ioScheduler())

    }
}