package com.skyyo.samples.features.pagination.simple

import com.skyyo.samples.application.models.Cat


sealed class CatsResult {
    class Success(val cats: List<Cat>, val lastPageReached: Boolean = false) : CatsResult()
    object NetworkError : CatsResult()
}
