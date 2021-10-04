package com.skyyo.samples.features.exoPlayer.columnIndexed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyyo.samples.features.exoPlayer.common.VideoItemImmutable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ExoPlayerColumnIndexedViewModel @Inject constructor() : ViewModel() {

    val videos = MutableLiveData<List<VideoItemImmutable>>()
    val currentlyPlayingIndex = MutableLiveData<Int?>()

    init {
        populateListWithFakeData()
    }

    private fun populateListWithFakeData() {
        val testVideos = listOf(
            VideoItemImmutable(
                1,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg"
            ),
            VideoItemImmutable(
                2,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"
            ),
            VideoItemImmutable(
                3,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg"
            ),
            VideoItemImmutable(
                4,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg"
            ),
            VideoItemImmutable(
                5,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg"
            ),
            VideoItemImmutable(
                6,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg"
            ),
            VideoItemImmutable(
                7,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerMeltdowns.jpg"
            ),
            VideoItemImmutable(
                8,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/Sintel.jpg"
            ),
            VideoItemImmutable(
                9,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/SubaruOutbackOnStreetAndDirt.jpg"
            ),
            VideoItemImmutable(
                10,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/TearsOfSteel.jpg"
            ),
        )
        videos.postValue(testVideos)
    }

    fun onPlayVideoClick(playbackPosition: Long, videoIndex: Int) {
        when (currentlyPlayingIndex.value) {
            // video is not playing at the moment
            null -> currentlyPlayingIndex.postValue(videoIndex)
            // this video is already playing
            videoIndex -> {
                currentlyPlayingIndex.postValue(null)
                videos.value = videos.value!!.toMutableList().also { list ->
                    list[videoIndex] = list[videoIndex].copy(lastPlayedPosition = playbackPosition)
                }
            }
            // video is playing, and we're requesting new video to play
            else -> {
                videos.value = videos.value!!.toMutableList().also { list ->
                    list[currentlyPlayingIndex.value!!] =
                        list[currentlyPlayingIndex.value!!].copy(lastPlayedPosition = playbackPosition)
                }
                currentlyPlayingIndex.postValue(videoIndex)
            }
        }
    }
}