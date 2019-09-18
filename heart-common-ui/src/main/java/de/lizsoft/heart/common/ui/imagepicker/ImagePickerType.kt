package de.lizsoft.heart.common.ui.imagepicker

sealed class ImagePickerType {
    object Gallery : ImagePickerType()
    object Camera : ImagePickerType()
}