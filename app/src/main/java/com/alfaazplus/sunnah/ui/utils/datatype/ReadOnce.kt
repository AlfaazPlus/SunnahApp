package com.alfaazplus.sunnah.ui.utils.datatype

import java.util.concurrent.atomic.AtomicReference

class ReadOnce<T>(initialValue: T? = null) {
    private val value = AtomicReference(initialValue)

    fun get(): T? = value.getAndSet(null)

    fun set(newValue: T) {
        value.set(newValue)
    }
}
