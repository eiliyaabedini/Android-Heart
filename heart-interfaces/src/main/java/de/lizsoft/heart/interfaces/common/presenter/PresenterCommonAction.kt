package de.lizsoft.heart.interfaces.common.presenter

sealed class PresenterCommonAction : PresenterAction {
    object ErrorRetryClicked : PresenterCommonAction()
}