package com.skyyo.igdbbrowser.features.signIn.googleMap

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.flowWithLifecycle
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.GoogleMapOptions
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.CameraPosition
import com.google.android.libraries.maps.model.Polyline
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.ktx.awaitMap
import com.skyyo.igdbbrowser.R
import com.skyyo.igdbbrowser.application.activity.MainActivity
import com.skyyo.igdbbrowser.features.signIn.THEME_AUTO
import com.skyyo.igdbbrowser.features.signIn.THEME_DARK
import com.skyyo.igdbbrowser.features.signIn.THEME_LIGHT
import com.skyyo.igdbbrowser.theme.mapStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle
    val mapBundle = rememberSaveable(key = "mapBundle") { Bundle() }
    val mapView = remember {
        MapView(context, GoogleMapOptions().mapId(mapStyle)).apply {
            id = View.generateViewId()
        }
    }
    var lastMapCameraPosition: CameraPosition? = remember { null }
    val markers = remember(viewModel.markers, lifecycleOwner) {
        viewModel.markers.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }
    val events = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
    }

    LaunchedEffect(Unit) {
        val googleMap = mapView.awaitMap()
        val clusterManager = ClusterManager<ClusterItem>(context, googleMap).apply {
            renderer = ClusterRender(context, googleMap, this)
        }
        googleMap.apply {
            uiSettings.apply {
                isMyLocationButtonEnabled = false
                isMapToolbarEnabled = false
                isCompassEnabled = false
            }
            setOnCameraMoveListener {
                if (lastMapCameraPosition == null || lastMapCameraPosition?.zoom != googleMap.cameraPosition.zoom) {
                    clusterManager.cluster()
                    lastMapCameraPosition = googleMap.cameraPosition
                }
            }
            setOnMapClickListener {
                viewModel.removePolyline()
            }
        }
        launch { observePoints(markers, clusterManager) }
        launch { observeEvents(events, googleMap, context) }
    }

    DisposableEffect(Unit) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(mapBundle)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> {
                    mapView.onSaveInstanceState(mapBundle)
                    mapView.onStop()
                }
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }

    AndroidView({ mapView })

}

suspend fun observePoints(
    lifecycleAwarePointsFlow: Flow<List<ClusterItem>>,
    clusterManager: ClusterManager<ClusterItem>
) {
    lifecycleAwarePointsFlow.collect { clusterItem ->
        clusterManager.clearItems()
        clusterManager.addItems(clusterItem)
        clusterManager.cluster()
    }
}

suspend fun observeEvents(
    lifecycleAwareEventsFlow: Flow<MapEvent>,
    googleMap: GoogleMap,
    context: Context
) {
    var finalPolyline: Polyline? = null
    lifecycleAwareEventsFlow.collect { event ->
        when (event) {
            is MapEvent.ShowKMLDraw -> {
                val layer = withContext(Dispatchers.Default) {
                    KmlLayer(googleMap, R.raw.map_style, context)
                }
                layer.addLayerToMap()
            }
            is MapEvent.ShowPolyline -> {
                finalPolyline = googleMap.addPolyline(event.options)
            }
            is MapEvent.RemovePolyline -> {
                finalPolyline?.remove()
            }
        }
    }
}
