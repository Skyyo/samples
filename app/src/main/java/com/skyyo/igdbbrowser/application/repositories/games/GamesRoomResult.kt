package com.skyyo.igdbbrowser.application.repositories.games


sealed class GamesRoomResult {
    object NetworkError : GamesRoomResult()
    object Success : GamesRoomResult()
    object LastPageReached : GamesRoomResult()
}
