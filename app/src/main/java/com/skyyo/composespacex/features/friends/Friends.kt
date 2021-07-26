package com.skyyo.composespacex.features.friends

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FriendsList(openFriendDetailsScreen: () -> Unit) {
    Button(onClick = openFriendDetailsScreen) {
        Text(text = "Friends")
    }

}

@Composable
fun FriendsDetails(viewModel: FriendsViewModel = hiltViewModel()) {
    Button(onClick = viewModel::onGoFriendContactClick) {
        Text("FriendsDetails: go FriendContactsScreen!")
    }
}

@Composable
fun FriendContactsScreen(navigateUp: () -> Unit) {
    Button(onClick = navigateUp) {
        Text(text = "FriendContactsScreen: go back")
    }
}