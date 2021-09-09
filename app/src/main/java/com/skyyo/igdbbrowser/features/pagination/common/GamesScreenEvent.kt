package com.skyyo.igdbbrowser.features.pagination.common

sealed class GamesScreenEvent {
    class ShowToast(val messageId: Int) : GamesScreenEvent()
    object ScrollToTop : GamesScreenEvent()
    object RefreshList : GamesScreenEvent()
}
