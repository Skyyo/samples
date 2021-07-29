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

sealed class DogDetailsGraph(val route: String) {
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

sealed class EditProfileGraph(val route: String) {

    object EditProfile : Screens("editProfile", R.string.edit_profile)
    object EditProfileConfirmation : Screens("profileConfirmation", R.string.profile_confirmation)
    object EditProfileConfirmation2 : Screens("profileConfirmation2", R.string.profile_confirmation)

    companion object {
        const val route = "profileGraph"
    }
}