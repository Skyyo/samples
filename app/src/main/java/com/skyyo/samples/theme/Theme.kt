package com.skyyo.samples.theme

import androidx.annotation.RawRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.skyyo.samples.R
import com.skyyo.samples.features.sampleContainer.THEME_DARK
import com.skyyo.samples.features.sampleContainer.THEME_LIGHT

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
const val DARK_MAP_STYLES = "d5afddd010938b78" // TODO use with cloud based styles
const val LIGHT_MAP_STYLES = "8d74d6ad37a6ca97"
@RawRes var mapStyle: Int? = null


@Composable
fun IgdbBrowserTheme(
    savedTheme: String,
    content: @Composable () -> Unit
) {
    val colors: Colors
    when (savedTheme) {
        THEME_LIGHT -> {
            colors = LightColorPalette
            mapStyle = null
        }
        THEME_DARK -> {
            colors = DarkColorPalette
            mapStyle = R.raw.map_dark_style
        }
        else -> {
            if (isSystemInDarkTheme()) {
                colors = DarkColorPalette
                mapStyle = R.raw.map_dark_style
            } else {
                colors = LightColorPalette
                mapStyle = null
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