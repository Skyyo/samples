package com.skyyo.samples.features.exoPlayer.columnAutoplay

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyyo.samples.features.exoPlayer.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ExoPlayerColumnAutoplayViewModel @Inject constructor() : ViewModel() {

    val videos = MutableLiveData<List<VideoItem>>()

    init {
        populateListWithFakeData()
    }

    private fun populateListWithFakeData() {
        val testVideos = listOf(
            VideoItem(
                1,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            ),
            VideoItem(
                2,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
            VideoItem(
                3,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            ),
            VideoItem(
                4,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
            ),
            VideoItem(
                5,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
            ),
            VideoItem(
                6,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
            ),
            VideoItem(
                7,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
            ),
            VideoItem(
                8,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
            ),
            VideoItem(
                9,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"
            ),
            VideoItem(
                10,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
            ),
        )
        videos.postValue(testVideos)
    }

}