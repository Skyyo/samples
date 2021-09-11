package com.skyyo.igdbbrowser.features.pagination.simple

import com.skyyo.igdbbrowser.application.models.remote.Game


sealed class GamesResult {
    class Success(val games: List<Game>, val lastPageReached: Boolean = false) : GamesResult()
    object NetworkError : GamesResult()
}
