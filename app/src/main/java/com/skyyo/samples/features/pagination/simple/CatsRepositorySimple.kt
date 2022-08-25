package com.skyyo.samples.features.pagination.simple

import com.skyyo.samples.application.CODE_200
import com.skyyo.samples.application.network.calls.CatCalls
import com.skyyo.samples.extensions.tryOrNull
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class CatsRepositorySimple @Inject constructor(private val catCalls: CatCalls) {

    suspend fun getCats(limit: Int, offset: Int): CatsResult {
        val response = tryOrNull { catCalls.getCats(offset, limit) }
        return when {
            response?.code() == CODE_200 -> {
                val cats = response.body()!!
                CatsResult.Success(cats = cats, lastPageReached = cats.size != limit)
            }
            else -> CatsResult.NetworkError
        }
    }
}
