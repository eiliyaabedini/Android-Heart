package de.lizsoft.heart.interfaces.common

import io.reactivex.Maybe
import org.koin.core.scope.Scope

abstract class PermissionHandler {
    abstract fun getPermission(scope: Scope, permission: String): Maybe<Boolean>
}