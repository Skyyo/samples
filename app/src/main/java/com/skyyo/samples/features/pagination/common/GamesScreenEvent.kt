package com.skyyo.samples.features.pagination.common

sealed class GamesScreenEvent {
    class ShowToast(val messageId: Int) : GamesScreenEvent()
    object ScrollToTop : GamesScreenEvent()
    object RefreshList : GamesScreenEvent()
}
