package com.skyyo.composespacex.features.launches

sealed class PastLaunchesListEvent {
    object NetworkError : PastLaunchesListEvent()
}
