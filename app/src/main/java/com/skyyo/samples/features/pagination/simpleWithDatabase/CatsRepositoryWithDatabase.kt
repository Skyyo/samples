package com.skyyo.samples.features.pagination.simpleWithDatabase

import com.skyyo.samples.application.models.Cat
import com.skyyo.samples.application.network.calls.CatCalls
import com.skyyo.samples.application.persistance.room.cats.CatsDao
import com.skyyo.samples.extensions.tryOrNull
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@ViewModelScoped
class CatsRepositoryWithDatabase @Inject constructor(
    private val calls: CatCalls,
    private val dao: CatsDao
) {

    fun observeCats(): Flow<List<Cat>> = dao.observeCats()

    suspend fun getCats(limit: Int, offset: Int): CatsRoomResult {
        val response = tryOrNull { calls.getCats(offset, limit) }
        return when {
            response?.code() == 200 -> {
                val cats = response.body()!!
                if (offset == 0) dao.deleteAndInsertCats(cats) else dao.insertCats(cats)
                CatsRoomResult.Success(lastPageReached = cats.size != limit)
            }
            else -> CatsRoomResult.NetworkError
        }
    }
}