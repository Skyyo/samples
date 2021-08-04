package com.skyyo.composespacex.application.repositories.launches


sealed class LatestLaunchResult {
    object NetworkError : LatestLaunchResult()
    object Success : LatestLaunchResult()
}
