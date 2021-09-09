package com.skyyo.igdbbrowser.features.pagination.simpleWithDatabase


sealed class GamesRoomResult {
    object NetworkError : GamesRoomResult()
    class Success(val lastPageReached: Boolean = false) : GamesRoomResult()
}
