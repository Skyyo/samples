package com.skyyo.igdbbrowser.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

const val AUTO = 0
const val LIGHT = 1
const val DARK = 2
const val DARK_MAP_STYLES = "d5afddd010938b78"
const val LIGHT_MAP_STYLES = "8d74d6ad37a6ca97"

var mapStyle: String = ""


@Composable
fun IgdbBrowserTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    mapStyle = when (darkTheme) {
        true -> LIGHT_MAP_STYLES
        false -> DARK_MAP_STYLES
//        else -> if (isSystemDarkTheme) DARK_MAP_STYLES else LIGHT_MAP_STYLES
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}