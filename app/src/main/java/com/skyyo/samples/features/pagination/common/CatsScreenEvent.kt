package com.skyyo.samples.features.pagination.common

sealed class CatsScreenEvent {
    class ShowToast(val messageId: Int) : CatsScreenEvent()
    object ScrollToTop : CatsScreenEvent()
    object RefreshList : CatsScreenEvent()
}
