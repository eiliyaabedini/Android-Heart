package de.lizsoft.heart.common.extension

fun Int?.isNotEmpty(): Boolean {
    return this != null && this > 0
}

fun Int?.isEmpty(): Boolean {
    return this == null || this == 0
}