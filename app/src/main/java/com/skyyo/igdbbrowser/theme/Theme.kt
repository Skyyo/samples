package com.skyyo.igdbbrowser.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.skyyo.igdbbrowser.extensions.log
import com.skyyo.igdbbrowser.features.signIn.THEME_DARK
import com.skyyo.igdbbrowser.features.signIn.THEME_LIGHT

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)
private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200
)
const val DARK_MAP_STYLES = "d5afddd010938b78"
const val LIGHT_MAP_STYLES = "8d74d6ad37a6ca97"
var mapStyle: String = ""


@Composable
fun IgdbBrowserTheme(
    savedTheme: String,
    content: @Composable () -> Unit
) {
    val colors: Colors
    when (savedTheme) {
        THEME_LIGHT -> {
            log("theme light")
            colors = LightColorPalette
            mapStyle = LIGHT_MAP_STYLES
        }
        THEME_DARK -> {
            log("theme dark")
            colors = DarkColorPalette
            mapStyle = DARK_MAP_STYLES
        }
        else -> {
            if (isSystemInDarkTheme()) {
                log("theme auto dark")
                colors = DarkColorPalette
                mapStyle = DARK_MAP_STYLES
            } else {
                log("theme auto light")
                colors = LightColorPalette
                mapStyle = LIGHT_MAP_STYLES
            }
        }
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}