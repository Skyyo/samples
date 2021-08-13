package com.skyyo.igdbbrowser.features.launches

sealed class PastLaunchesListEvent {
    object NetworkError : PastLaunchesListEvent()
}
