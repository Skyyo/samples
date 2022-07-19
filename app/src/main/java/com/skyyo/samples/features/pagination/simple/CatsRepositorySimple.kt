package com.skyyo.samples.features.pagination.simple

import com.skyyo.samples.application.network.calls.CatCalls
import com.skyyo.samples.extensions.tryOrNull
import com.skyyo.samples.features.pagination.simpleWithDatabase.CODE_200
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class CatsRepositorySimple @Inject constructor(private val calls: CatCalls) {

    suspend fun getCats(limit: Int, offset: Int): CatsResult {
        val response = tryOrNull { calls.getCats(offset, limit) }
        return when {
            response?.code() == CODE_200 -> {
                val cats = response.body()!!
                CatsResult.Success(cats = cats, lastPageReached = cats.size != limit)
            }
            else -> CatsResult.NetworkError
        }
    }
}
