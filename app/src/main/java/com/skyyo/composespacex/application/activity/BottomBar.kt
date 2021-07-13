package com.skyyo.composespacex.application.activity

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.skyyo.composespacex.application.Screens

@Composable
fun BottomBar(
    items: List<Screens>,
    selectedIndex: Int,
    onTabClick: (index: Int, route: String) -> Unit
) {
    return BottomNavigation {
        items.forEachIndexed { index, screen ->
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                label = { Text(stringResource(screen.resourceId)) },
                selected = index == selectedIndex,
                onClick = { onTabClick(index, screen.route) }
            )
        }
    }
}