package com.skyyo.igdbbrowser.application.repositories.games

import com.skyyo.igdbbrowser.application.models.remote.Game


sealed class GamesResult {
    class Success(val games: List<Game>) : GamesResult()
    object NetworkError : GamesResult()
    object LastPageReached : GamesResult()
}
