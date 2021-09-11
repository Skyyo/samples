package com.skyyo.samples.features.pagination.simpleWithDatabase


sealed class GamesRoomResult {
    object NetworkError : GamesRoomResult()
    class Success(val lastPageReached: Boolean = false) : GamesRoomResult()
}
