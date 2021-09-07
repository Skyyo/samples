package com.skyyo.igdbbrowser.application.repositories.games

import com.skyyo.igdbbrowser.application.models.remote.Game

sealed class GamesPagingResult {
    class Success(val games: List<Game>, val lastPageReached: Boolean = false) : GamesPagingResult()
    object NetworkError : GamesPagingResult()
}