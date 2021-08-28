package com.skyyo.igdbbrowser.application.activity.cores.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerItem(title: String, selected: Boolean, onClick: () -> Unit) {
    //TODO is remember needed here
    val backgroundColourId = when {
        selected -> android.R.color.holo_blue_light
        else -> android.R.color.transparent
    }
    Text(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50))
            .clickable(onClick = { onClick() })
            .background(colorResource(id = backgroundColourId))
            .padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
        text = title,
        fontSize = 18.sp,
        color = Color.Black
    )
}
