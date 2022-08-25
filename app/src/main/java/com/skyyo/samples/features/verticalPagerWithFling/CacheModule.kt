package com.skyyo.samples.features.verticalPagerWithFling

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.hls.offline.HlsDownloader
import com.google.android.datatransport.runtime.ExecutionModule_ExecutorFactory.executor
import com.skyyo.samples.extensions.log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.chromium.net.CronetEngine

private const val PRE_CACHE_AMOUNT = 6 * 1048576L

class CacheModule(context: Context) {
    private var exoplayerDatabaseProvider = StandaloneDatabaseProvider(context)
    private val cronetEngine = CronetEngine.Builder(context).build()
    private val cronetDataSourceFactory = CronetDataSource.Factory(cronetEngine, executor())
    private val cacheReadDataSourceFactory = FileDataSource.Factory()
    private val cacheSize = Runtime.getRuntime().maxMemory() / 6
    private var cacheEvictor = LeastRecentlyUsedCacheEvictor(cacheSize)
    private var cache = SimpleCache(context.cacheDir, cacheEvictor, exoplayerDatabaseProvider)
//    private val cacheSink = CacheDataSink.Factory().setCache(cache)
    val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(cache)
//        .setCacheWriteDataSinkFactory(cacheSink)
        .setCacheReadDataSourceFactory(cacheReadDataSourceFactory)
        .setUpstreamDataSourceFactory(cronetDataSourceFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    fun isUriCached(uri: String, position: Long = 0): Boolean {
        return cache.isCached(uri, position, PRE_CACHE_AMOUNT)
    }

    // TODO add the same for mp4. Also they might be a much better option, since they only have
    // single track, so no matter what connection you have - loading can't happen twice
    fun getHlsMediaSource(mediaItem: MediaItem): HlsMediaSource {
        return HlsMediaSource.Factory(cacheDataSourceFactory)
            .setAllowChunklessPreparation(true)
            .createMediaSource(mediaItem)
    }

    fun releaseCache() = cache.release()

    suspend fun preCacheUri(mediaItem: MediaItem) {
        val downloader = HlsDownloader(mediaItem, cacheDataSourceFactory)
        withContext(Dispatchers.IO) {
            try {
                downloader.download { _, bytesDownloaded, _ ->
                    if (bytesDownloaded >= PRE_CACHE_AMOUNT) {
//                        log("video precached at $percent%")
                        downloader.cancel()
                    }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) log("precache exception $e")
            }
        }
    }
}
