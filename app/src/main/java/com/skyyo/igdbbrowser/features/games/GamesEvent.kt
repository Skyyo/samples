package com.skyyo.igdbbrowser.features.games

sealed class GamesEvent {
    object NetworkError : GamesEvent()
}
