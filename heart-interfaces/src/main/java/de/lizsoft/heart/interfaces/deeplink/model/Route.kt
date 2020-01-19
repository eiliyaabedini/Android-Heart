package de.lizsoft.heart.interfaces.deeplink.model

data class Route(val route: String, val value: String)

fun Route.hasValue() : Boolean = !route.isBlank() && !value.isBlank()