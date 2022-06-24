package com.skyyo.samples.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine

inline fun <T> tryOrNull(f: () -> T) =
    try {
        f()
    } catch (e: Exception) {
        log("exception: $e")
        null
    }

fun log(message: String, tag: String = "vovk") {
    Log.d(tag, message)
}

fun Context.toast(messageId: Int) {
    Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

// don't use unless the difference between liveData & stateFlow for
// scenarios with search/flatMapLatest is used
fun <T> SavedStateHandle.getStateFlow(
    scope: CoroutineScope,
    key: String,
    initialValue: T
): MutableStateFlow<T> {
    val liveData = getLiveData(key, initialValue)
    val stateFlow = MutableStateFlow(initialValue)

    val observer = Observer<T> { value -> if (value != stateFlow.value) stateFlow.value = value }
    liveData.observeForever(observer)

    stateFlow.onCompletion {
        withContext(Dispatchers.Main.immediate) {
            liveData.removeObserver(observer)
        }
    }.onEach { value ->
        withContext(Dispatchers.Main.immediate) {
            if (liveData.value != value) liveData.value = value
        }
    }.launchIn(scope)

    return stateFlow
}

fun Context.goAppPermissions() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse("package:${applicationContext.packageName}")
    }
    startActivity(intent)
}

inline fun <reified T : Any> Flow<PagingData<T>>.toLazyPagingItems(): LazyPagingItems<T> {
    val constructor = LazyPagingItems::class.java.constructors.first { it.parameterCount == 1 && it.parameterTypes[0] == Flow::class.java }
    constructor.isAccessible = true
    return constructor.newInstance(this) as LazyPagingItems<T>
}

@Composable
inline fun <reified T: Any> LazyPagingItems<T>.collect(): LazyPagingItems<T> {
    val items = this
    val methods = LazyPagingItems::class.java.declaredMethods
    val collectPagingDataMethod = methods.first { it.name.contains("collectPagingData") }
    collectPagingDataMethod.isAccessible = true
    LaunchedEffect(items) {
        suspendCoroutine<Unit> {
            collectPagingDataMethod.invoke(items, it)
        }
    }

    val collectLoadStateMethod = methods.first { it.name.contains("collectLoadState") }
    collectLoadStateMethod.isAccessible = true
    LaunchedEffect(items) {
        suspendCoroutine<Unit> {
            collectLoadStateMethod.invoke(items, it)
        }
    }

    return items
}