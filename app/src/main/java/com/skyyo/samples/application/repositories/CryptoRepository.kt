package com.skyyo.samples.application.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.skyyo.samples.application.models.Asset
import com.skyyo.samples.application.models.PriceFluctuation
import com.skyyo.samples.application.network.calls.CryptoCalls
import com.skyyo.samples.application.persistance.room.AppDatabase
import com.skyyo.samples.application.persistance.room.assets.AssetsDao
import com.skyyo.samples.application.persistance.room.assets.AssetsRemoteKeysDao
import com.skyyo.samples.features.realTimeUpdates.AssetsRemoteMediator
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject

@ViewModelScoped
class CryptoRepository @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val calls: CryptoCalls,
    private val assetsDao: AssetsDao,
    private val assetsRemoteKeysDao: AssetsRemoteKeysDao,
    private val appDatabase: AppDatabase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun subscribeToPriceUpdates(id: String) = callbackFlow {
        val request = Request.Builder()
            .url("wss://ws.coincap.io/prices?assets=$id")
            .build()
        val socket = okHttpClient.newWebSocket(
            request = request,
            listener = object : WebSocketListener() {
                override fun onMessage(webSocket: WebSocket, text: String) {
                    trySend(JSONObject(text).getString(id))
                }
            }
        )
        awaitClose {
            socket.close(1000, null)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getAssets(): Pager<Int, Asset> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 15,
                enablePlaceholders = false
            ),
            remoteMediator = AssetsRemoteMediator(
                appDatabase = appDatabase,
                calls = calls,
                assetsDao = assetsDao,
                assetsRemoteKeysDao = assetsRemoteKeysDao
            ),
            pagingSourceFactory = { assetsDao.getPagingSource() }
        )
    }

    suspend fun updateAsset(id: String, price: String) {
        val asset = assetsDao.getAssetById(id)
        assetsDao.updateAsset(
            asset.copy(
                priceUsd = price,
                priceFluctuation = when {
                    asset.priceUsd.toFloat() > price.toFloat() -> PriceFluctuation.Down
                    asset.priceUsd.toFloat() < price.toFloat() -> PriceFluctuation.Up
                    else -> PriceFluctuation.Unknown
                }
            )
        )
    }
}