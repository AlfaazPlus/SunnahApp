package com.alfaazplus.sunnah.ui.controllers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class ModalController<T> {
    private val _show = mutableStateOf(false)
    private val _data = mutableStateOf<T?>(null)

    val isVisible: Boolean get() = _show.value
    val data: T? get() = _data.value

    fun show(data: T? = null) {
        _data.value = data
        _show.value = true
    }

    fun hide() {
        _show.value = false
        _data.value = null
    }
}

@Composable
fun <T> rememberModalController(): ModalController<T> {
    return remember { ModalController() }
}