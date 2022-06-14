package android.window

// return true, if callback handled, false otherwise
data class OnBackInvokedCallback(val action: () -> Boolean)
class OnBackInvokedDispatcher {
    private val callbacks: MutableList<OnBackInvokedCallback> = mutableListOf()

    fun registerOnBackInvokedCallback(priority: Int, onBackInvokedCallback: OnBackInvokedCallback) {
        if (priority == PRIORITY_OVERLAY) callbacks.add(onBackInvokedCallback)
    }

    fun unregisterOnBackInvokedCallback(onBackInvokedCallback: OnBackInvokedCallback) {
        callbacks.remove(onBackInvokedCallback)
    }

    // should be called only from activity
    fun handleOnBackPress(): Boolean {
        for (callback in callbacks) {
            if (callback.action()) return true
        }
        return false
    }

    companion object {
        const val PRIORITY_OVERLAY = 10
    }
}