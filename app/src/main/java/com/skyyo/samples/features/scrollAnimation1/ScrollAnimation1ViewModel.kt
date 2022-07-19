package com.skyyo.samples.features.scrollAnimation1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScrollAnimation1ViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher
) : ViewModel() {

    val text = MutableLiveData(mutableListOf<String>())

    init {
        repeat(times = 10) {
            text.value!!.add("awdawdawdawdadawdwadawdawdad awdawdawawwadawwda awdwawadwdwadadw adawdadwdwawdawd awdwadadwwaddwdawdaw")
        }
    }
}
