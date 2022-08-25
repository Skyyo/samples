package com.skyyo.samples.features.pagination.simpleWithDatabase

import com.skyyo.samples.application.CODE_200
import com.skyyo.samples.application.models.Cat
import com.skyyo.samples.application.network.calls.CatCalls
import com.skyyo.samples.application.persistance.room.cats.CatsDao
import com.skyyo.samples.extensions.tryOrNull
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class CatsRepositoryWithDatabase @Inject constructor(
    private val catCalls: CatCalls,
    private val catsDao: CatsDao
) {

    fun observeCats(): Flow<List<Cat>> = catsDao.observeCats()

    suspend fun getCats(limit: Int, offset: Int): CatsRoomResult {
        val response = tryOrNull { catCalls.getCats(offset, limit) }
        return when {
            response?.code() == CODE_200 -> {
                val cats = response.body()!!
                if (offset == 0) catsDao.deleteAndInsertCats(cats) else catsDao.insertCats(cats)
                CatsRoomResult.Success(lastPageReached = cats.size != limit)
            }
            else -> CatsRoomResult.NetworkError
        }
    }
}
