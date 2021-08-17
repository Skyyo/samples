package com.skyyo.igdbbrowser.application.repositories.games

import com.skyyo.igdbbrowser.application.models.remote.Game


sealed class GamesResult {
    object NetworkError : GamesResult()
    object Success : GamesResult()
    class SuccessWithoutDatabase(val games: List<Game>) : GamesResult()
    object LastPageReached : GamesResult()
}
