package de.lizsoft.heart.interfaces

sealed class ResponseResult<out T> {
    data class Success<out T>(val data: T, val fromCache: Boolean = false) : ResponseResult<T>()
    data class Failure<out T>(val error: Throwable) : ResponseResult<T>()
}