package com.skyyo.igdbbrowser.features.games

sealed class GamesListEvent {
    object NetworkError : GamesListEvent()
}
