package com.skyyo.samples.features.pagination.simpleWithDatabase

sealed class CatsRoomResult {
    object NetworkError : CatsRoomResult()
    class Success(val lastPageReached: Boolean = false) : CatsRoomResult()
}
