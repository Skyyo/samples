package com.skyyo.samples.features.dominantColor

import android.content.Context
import androidx.collection.LruCache
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.palette.graphics.Palette
import coil.Coil
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DominantColorScreen(viewModel: DominantColorViewModel = hiltViewModel()) {

    val cats = viewModel.cats.collectAsLazyPagingItems()
    val pagerState = rememberPagerState(pageCount = cats.itemCount)
    val bgColor = MaterialTheme.colors.background
    val dominantColorState =
        rememberDominantColorState(isColorValid = { it.contrastAgainst(bgColor) > 3f })
    LaunchedEffect(pagerState.currentPage, pagerState.pageCount) {
        if (cats.itemCount > 0) {
            val id = cats.peek(pagerState.currentPage)?.id
            dominantColorState.updateColorsFromImageUrl("https://cataas.com/cat/$id")
        }
    }
    DynamicTheme(dominantColorState) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            state = pagerState
        ) {
            val catId = cats[it]?.id
            CatCard(catId = catId)
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CatCard(catId: String?) {
    /* To avoid this https://github.com/Skyyo/samples/pull/15#pullrequestreview-787405549
    we should limit size of Card or Image, so they couldn't fill all height while fetching image **/
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(ratio = .85f),
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Column {
            Image(
                painter = rememberImagePainter(
                    data = "https://cataas.com/cat/$catId",
                    builder = { transformations(CircleCropTransformation()) }
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(catId.orEmpty())
        }
    }
}

fun Color.contrastAgainst(background: Color): Float {
    val fg = if (alpha < 1f) compositeOver(background) else this

    val fgLuminance = fg.luminance() + 0.05f
    val bgLuminance = background.luminance() + 0.05f

    return max(fgLuminance, bgLuminance) / min(fgLuminance, bgLuminance)
}

@Composable
fun DynamicTheme(
    dominantColorState: DominantColorState = rememberDominantColorState(),
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colors.copy(
        primary = animateColorAsState(
            dominantColorState.color,
            spring(stiffness = Spring.StiffnessLow)
        ).value,
        onPrimary = animateColorAsState(
            dominantColorState.onColor,
            spring(stiffness = Spring.StiffnessLow)
        ).value
    )
    MaterialTheme(colors = colors) { content() }
}

@Composable
fun rememberDominantColorState(
    context: Context = LocalContext.current,
    defaultColor: Color = MaterialTheme.colors.primary,
    defaultOnColor: Color = MaterialTheme.colors.onPrimary,
    cacheSize: Int = 12,
    isColorValid: (Color) -> Boolean = { true }
): DominantColorState = remember {
    DominantColorState(context, defaultColor, defaultOnColor, cacheSize, isColorValid)
}

class DominantColorState(
    private val context: Context,
    private val defaultColor: Color,
    private val defaultOnColor: Color,
    cacheSize: Int = 12,
    private val isColorValid: (Color) -> Boolean = { true }
) {
    var color by mutableStateOf(defaultColor)
        private set
    var onColor by mutableStateOf(defaultOnColor)
        private set

    private val cache = when {
        cacheSize > 0 -> LruCache<String, DominantColors>(cacheSize)
        else -> null
    }

    suspend fun updateColorsFromImageUrl(url: String) {
        val result = calculateDominantColor(url)
        color = result?.backgroundColor ?: defaultColor
        onColor = result?.contentColor ?: defaultOnColor
    }

    private suspend fun calculateDominantColor(url: String): DominantColors? {
        val cached = cache?.get(url)
        if (cached != null) {
            // If we already have the result cached, return early now...
            return cached
        }

        // Otherwise we calculate the swatches in the image, and return the first valid color
        return calculateSwatchesInImage(context, url)
            // First we want to sort the list by the color's population
            .sortedByDescending { swatch -> swatch.population }
            // Then we want to find the first valid color
            .firstOrNull { swatch -> isColorValid(Color(swatch.rgb)) }
            // If we found a valid swatch, wrap it in a [DominantColors]
            ?.let { swatch ->
                DominantColors(
                    backgroundColor = Color(swatch.rgb),
                    contentColor = Color(swatch.bodyTextColor).copy(alpha = 1f)
                )
            }
            // Cache the resulting [DominantColors]
            ?.also { result -> cache?.put(url, result) }
    }

    /**
     * Reset the color values to [defaultColor].
     */
    fun reset() {
        color = defaultColor
        onColor = defaultColor
    }

    private suspend fun calculateSwatchesInImage(
        context: Context,
        imageUrl: String
    ): List<Palette.Swatch> {
        val r = ImageRequest.Builder(context)
            .data(imageUrl)
            // We scale the image to cover 128px x 128px (i.e. min dimension == 128px)
            .size(128).scale(Scale.FILL)
            // Disable hardware bitmaps, since Palette uses Bitmap.getPixels()
            .allowHardware(false)
            .build()

        val bitmap = when (val result = Coil.execute(r)) {
            is SuccessResult -> result.drawable.toBitmap()
            else -> null
        }

        return bitmap?.let {
            withContext(Dispatchers.Default) {
                val palette = Palette.Builder(bitmap)
                    // Disable any bitmap resizing in Palette. We've already loaded an appropriately
                    // sized bitmap through Coil
                    .resizeBitmapArea(0)
                    // Clear any built-in filters. We want the unfiltered dominant color
                    .clearFilters()
                    // We reduce the maximum color count down to 8
                    .maximumColorCount(8)
                    .generate()

                palette.swatches
            }
        } ?: emptyList()
    }
}
