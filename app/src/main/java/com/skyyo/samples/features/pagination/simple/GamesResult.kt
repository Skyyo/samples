package com.skyyo.samples.features.pagination.simple

import com.skyyo.samples.application.models.remote.Game


sealed class GamesResult {
    class Success(val games: List<Game>, val lastPageReached: Boolean = false) : GamesResult()
    object NetworkError : GamesResult()
}
