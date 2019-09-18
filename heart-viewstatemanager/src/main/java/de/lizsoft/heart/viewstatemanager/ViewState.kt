package de.lizsoft.heart.viewstatemanager

sealed class ViewState {
    object Normal : ViewState()
    object Loading : ViewState()
    object Error : ViewState()
    object NONE : ViewState() //Just for tracking, This state will not come from view side
}