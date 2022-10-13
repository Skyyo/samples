package com.skyyo.samples.features.verticalPagerWithFling

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyyo.samples.features.exoPlayer.common.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerticalPagerWithFlingViewModel @Inject constructor() : ViewModel() {

    val videos = MutableLiveData<List<VideoItem>>()

    init {
        populateListWithFakeData()
    }

    @Suppress("LongMethod", "MagicNumber")
    private fun populateListWithFakeData() {
//        val testVideos = listOf(
//            VideoItem(
//                1,
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg"
//            ),
//            VideoItem(
//                2,
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"
//            ),
//            VideoItem(
//                3,
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg"
//            ),
//            VideoItem(
//                4,
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg"
//            ),
//            VideoItem(
//                5,
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg"
//            ),
//            VideoItem(
//                6,
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg"
//            ),
//            VideoItem(
//                7,
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerMeltdowns.jpg"
//            ),
//            VideoItem(
//                8,
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/Sintel.jpg"
//            ),
//            VideoItem(
//                9,
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/SubaruOutbackOnStreetAndDrift.jpg"
//            ),
//            VideoItem(
//                10,
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/TearsOfSteel.jpg"
//            ),
//        )
        val testVideosHlsApple = listOf(
            VideoItem(
                1,
                "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg"
            ),
            VideoItem(
                2,
                "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"
            ),
            VideoItem(
                3,
                "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg"
            ),
            VideoItem(
                4,
                "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg"
            ),
            VideoItem(
                5,
                "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/gear0/prog_index.m3u8",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg"
            )
        )
        videos.postValue(testVideosHlsApple)
    }
}
