package com.skyyo.igdbbrowser.application.repositories.games


sealed class LatestLaunchResult {
    object NetworkError : LatestLaunchResult()
    object Success : LatestLaunchResult()
}
