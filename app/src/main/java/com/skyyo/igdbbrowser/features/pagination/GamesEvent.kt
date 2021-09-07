package com.skyyo.igdbbrowser.features.pagination

sealed class GamesEvent {
    object NetworkError : GamesEvent()
    object ScrollToTop : GamesEvent()
    object RefreshList : GamesEvent()
}
