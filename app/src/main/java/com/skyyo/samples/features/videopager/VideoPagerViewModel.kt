package com.skyyo.samples.features.videopager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyyo.samples.features.exoPlayer.common.VideoItemImmutable
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class VideoPagerViewModel @Inject constructor() : ViewModel() {

    val videos = MutableStateFlow(listOf<String>())

    init {
        populateListWithFakeData()
    }

    private fun populateListWithFakeData() {
        val testVideos = listOf(
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
        )
        videos.value = testVideos
    }
}