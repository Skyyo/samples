package com.skyyo.igdbbrowser.features.games

sealed class GamesEvent {
    object NetworkError : GamesEvent()
    object ScrollToTop : GamesEvent()
    object RefreshList : GamesEvent()
}
