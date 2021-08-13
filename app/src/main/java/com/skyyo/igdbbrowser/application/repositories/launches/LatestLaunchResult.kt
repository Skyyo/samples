package com.skyyo.igdbbrowser.application.repositories.launches


sealed class LatestLaunchResult {
    object NetworkError : LatestLaunchResult()
    object Success : LatestLaunchResult()
}
