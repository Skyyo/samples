package com.skyyo.composespacex.features.friends

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun FriendsList(openFriendDetailsScreen: () -> Unit) {
    Button(onClick = openFriendDetailsScreen) {
        Text(text = "Friends")
    }

}

@Composable
fun FriendsDetails(openFriendContactsScreen: () -> Unit) {
    Button(onClick = openFriendContactsScreen) {
        Text("FriendsDetails: go FriendContactsScreen!")
    }
}

@Composable
fun FriendContactsScreen(navigateUp: () -> Unit) {
    Button(onClick = navigateUp) {
        Text(text = "FriendContactsScreen: go back")
    }
}