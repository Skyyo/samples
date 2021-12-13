package com.skyyo.samples.features.realTimeUpdates

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.systemBarsPadding
import com.skyyo.samples.application.models.Asset
import com.skyyo.samples.application.models.PriceFluctuation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

@Composable
fun RealTimeUpdatesScreen(viewModel: RealTimeUpdatesViewModel = hiltViewModel()) {
    val assets = viewModel.assets.collectAsLazyPagingItems()
    Scaffold(modifier = Modifier.systemBarsPadding()) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assets, key = { it.id }) { asset ->
                if (asset != null) {
                    AssetCard(
                        asset = asset,
                        priceFlow = viewModel.providePriceUpdateFlow(asset.id),
                        onPriceUpdate = { viewModel.onPriceUpdate(asset.id, it) }
                    )
                }
            }
        }
    }
}

@Composable
fun AssetCard(
    asset: Asset,
    priceFlow: Flow<String>,
    onPriceUpdate: suspend (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareFlow = remember(priceFlow, lifecycleOwner) {
        priceFlow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }
    LaunchedEffect(Unit) { lifecycleAwareFlow.collect(onPriceUpdate) }
    val accentColor by animateColorAsState(
        when (asset.priceFluctuation) {
            PriceFluctuation.Unknown -> Color.Yellow
            PriceFluctuation.Up -> Color.Green
            PriceFluctuation.Down -> Color.Red
        }
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = buildAnnotatedString {
                    append(asset.name)
                    append('\n')
                    withStyle(SpanStyle(color = accentColor)) {
                        append(String.format("%.5f", asset.priceUsd.toFloat()))
                    }
                }
            )
            Icon(
                imageVector = when (asset.priceFluctuation) {
                    PriceFluctuation.Unknown -> Icons.Filled.TrendingFlat
                    PriceFluctuation.Up -> Icons.Filled.TrendingUp
                    PriceFluctuation.Down -> Icons.Filled.TrendingDown
                },
                contentDescription = null,
                tint = accentColor
            )
        }
    }
}