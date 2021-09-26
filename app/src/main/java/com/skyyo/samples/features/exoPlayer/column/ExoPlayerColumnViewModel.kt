package com.skyyo.samples.features.exoPlayer.column

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyyo.samples.features.exoPlayer.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ExoPlayerColumnViewModel @Inject constructor() : ViewModel() {

    val videos = MutableLiveData<List<VideoItem>>()
    val currentlyPlayingId = MutableLiveData<Int>()

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

    fun onPlayVideoClick(id: Int) {
        //if id == currentlyPlayingId -  the same video was clicked again, we should stop the playback
        when (currentlyPlayingId.value) {
            id -> currentlyPlayingId.postValue(-1)
            else -> currentlyPlayingId.postValue(id)
        }
    }
}