package com.skyyo.composespacex.application.repositories.launches


sealed class PastLaunchesResult {
    object NetworkError : PastLaunchesResult()
    object Success : PastLaunchesResult()
    object LastPageReached : PastLaunchesResult()
}
