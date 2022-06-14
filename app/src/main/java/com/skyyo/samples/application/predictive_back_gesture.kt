package com.skyyo.samples.application

private typealias BackInvokedCallback = () -> Boolean

class BackInvokedDispatcher {
    private val callbacks = mutableListOf<BackInvokedCallback>()

    fun registerOnBackInvokedCallback(callback: BackInvokedCallback) {
        callbacks.add(callback)
    }

    fun unregisterOnBackInvokedCallback(callback: BackInvokedCallback) {
        callbacks.remove(callback)
    }

    fun handleBackPress(): Boolean {
        for (callback in callbacks) {
            if (callback.invoke()) return true
        }
        return false
    }
}