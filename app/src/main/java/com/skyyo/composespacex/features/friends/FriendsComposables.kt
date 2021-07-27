package com.skyyo.composespacex.features.friends

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FriendsList(viewModel: FriendsListViewModel = hiltViewModel()) {
    Button(onClick = viewModel::goFriendDetails) {
        Text(text = "Friends, open friend details screen")
    }

}

@Composable
fun FriendsDetails(viewModel: FriendDetailsViewModel = hiltViewModel()) {
    Button(onClick = viewModel::onGoFriendContactClick) {
        Text("FriendsDetails: go FriendContactsScreen!")
    }
}

@Composable
fun FriendContactsScreen(viewModel: FriendContactsViewModel = hiltViewModel()) {
    Button(onClick = viewModel::popToFriendsList) {
        Text(text = "FriendContactsScreen: pop to friends list")
    }
}