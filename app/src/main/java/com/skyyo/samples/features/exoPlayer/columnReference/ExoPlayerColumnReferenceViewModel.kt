package com.skyyo.samples.features.exoPlayer.columnReference

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyyo.samples.features.exoPlayer.common.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ExoPlayerColumnReferenceViewModel @Inject constructor() : ViewModel() {

    val videos = MutableLiveData<List<VideoItem>>()
    val currentlyPlayingItem = MutableLiveData<VideoItem?>()

    init {
        populateListWithFakeData()
    }

    private fun populateListWithFakeData() {
        val testVideos = listOf(
            VideoItem(
                1,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg"
            ),
            VideoItem(
                2,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"
            ),
            VideoItem(
                3,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg"
            ),
            VideoItem(
                4,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg"
            ),
            VideoItem(
                5,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg"
            ),
            VideoItem(
                6,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg"
            ),
            VideoItem(
                7,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerMeltdowns.jpg"
            ),
            VideoItem(
                8,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/Sintel.jpg"
            ),
            VideoItem(
                9,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/SubaruOutbackOnStreetAndDirt.jpg"
            ),
            VideoItem(
                10,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/TearsOfSteel.jpg"
            ),
        )
        videos.postValue(testVideos)
    }

    fun onPlayVideoClick(playbackPosition: Long, item: VideoItem?) {
        when (currentlyPlayingItem.value) {
            // video is not playing at the moment
            null -> currentlyPlayingItem.postValue(item)
            // this video is already playing
            item -> {
                currentlyPlayingItem.postValue(null)
                videos.value = videos.value!!.toMutableList().also { list ->
                    list.find { it == item }?.lastPlayedPosition = playbackPosition
                }
            }
            // video is playing, and we're requesting new video to play
            else -> {
                videos.value = videos.value!!.toMutableList().also { list ->
                    list.find {
                        it == currentlyPlayingItem.value
                    }?.lastPlayedPosition = playbackPosition
                }
                currentlyPlayingItem.postValue(item)
            }
        }
    }

}