package com.skyyo.composespacex.application.activity.cores.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.skyyo.composespacex.application.Screens

@Composable
fun Drawer(
    screens: List<Screens>,
    selectedTab: Int,
    onTabClick: (index: Int, route: String) -> Unit
) {
    Column {
        Spacer(modifier = Modifier.weight(1f))
        screens.forEachIndexed { index, screen ->
            DrawerItem(item = screen, selected = index == selectedTab) {
                onTabClick(index, screen.route)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}