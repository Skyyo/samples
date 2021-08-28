package com.skyyo.igdbbrowser.application.activity.cores.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    Button(
        shape = RoundedCornerShape(bottomEndPercent = 50, topEndPercent = 50),
        onClick = onClick,
        elevation = null,
        colors = buttonColors(backgroundColor = colorResource(id = backgroundColourId)),
        contentPadding = PaddingValues(),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
            text = title,
            fontSize = 18.sp,
            color = Color.Black)
    }
}
