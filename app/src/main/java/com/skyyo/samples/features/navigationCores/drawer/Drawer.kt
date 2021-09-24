package com.skyyo.samples.features.navigationCores.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.samples.application.Destination

@Composable
fun Drawer(
    screens: List<Destination>,
    selectedTab: Int,
    onTabClick: (index: Int, route: String) -> Unit
) {
    Column(Modifier.statusBarsPadding().navigationBarsPadding()) {
        screens.forEachIndexed { index, screen ->
            DrawerItem(
                title = "Tab $index ",
                selected = index == selectedTab
            ) { onTabClick(index, screen.route) }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
