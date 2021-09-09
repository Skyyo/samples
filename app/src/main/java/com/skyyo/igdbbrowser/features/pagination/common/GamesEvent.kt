package com.skyyo.igdbbrowser.features.pagination.common

sealed class GamesEvent {
    object NetworkError : GamesEvent()
    class ShowToast(val stringId: Int) : GamesEvent()
    object ScrollToTop : GamesEvent()
    object RefreshList : GamesEvent()
}
