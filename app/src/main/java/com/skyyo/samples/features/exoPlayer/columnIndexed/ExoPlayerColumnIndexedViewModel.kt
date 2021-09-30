package com.skyyo.samples.features.exoPlayer.columnIndexed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyyo.samples.features.exoPlayer.VideoItemImmutable
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
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            ),
            VideoItemImmutable(
                2,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
            VideoItemImmutable(
                3,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            ),
            VideoItemImmutable(
                4,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
            ),
            VideoItemImmutable(
                5,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
            ),
            VideoItemImmutable(
                6,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
            ),
            VideoItemImmutable(
                7,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
            ),
            VideoItemImmutable(
                8,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
            ),
            VideoItemImmutable(
                9,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"
            ),
            VideoItemImmutable(
                10,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
            ),
        )
        videos.postValue(testVideos)
    }

    fun onPlayVideoClick(playbackPosition: Long, videoIndex: Int) {
        // if videoIndex == currentlyPlayingIndex - the same video was clicked again,
        // we should stop the playback
        when (currentlyPlayingIndex.value) {
            videoIndex -> {
                currentlyPlayingIndex.postValue(null)
                videos.value = videos.value!!.toMutableList().also { list ->
                    list[videoIndex] = list[videoIndex].copy(lastPlayedPosition = playbackPosition)
                }
            }
            else -> currentlyPlayingIndex.postValue(videoIndex)
        }
    }
}