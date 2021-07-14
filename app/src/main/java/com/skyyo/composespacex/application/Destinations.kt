package com.skyyo.composespacex.application

import androidx.annotation.StringRes
import com.skyyo.composespacex.R

sealed class Screens(val route: String, @StringRes val resourceId: Int = 0) {
    object AuthScreen : Screens("auth", R.string.auth)
    object DogFeedScreen : Screens("dogFeed", R.string.dog_feed)
    object Profile : Screens("profile", R.string.profile)
    object FriendsList : Screens("friendslist", R.string.friends_list)
    object FriendDetails : Screens("friendDetails")
    object FriendContacts : Screens("friendContacts")
}

sealed class DogDetailsGraph(val route: String = "dogDetailsGraph") {
    object DogDetails : DogDetailsGraph("dogDetails/{dogId}") {
        fun createRoute(dogId: String) = "dogDetails/$dogId"
    }

    object DogContacts : DogDetailsGraph("dogContacts/{dogId}") {
        fun createRoute(dogId: String) = "dogContacts/$dogId"
    }

    companion object {
        const val route = "dogDetailsGraph"
    }
}