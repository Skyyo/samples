package com.skyyo.igdbbrowser.application.repositories.games


sealed class PastLaunchesResult {
    object NetworkError : PastLaunchesResult()
    object Success : PastLaunchesResult()
    object LastPageReached : PastLaunchesResult()
}
