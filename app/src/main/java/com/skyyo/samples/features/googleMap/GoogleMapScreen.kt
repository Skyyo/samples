package com.skyyo.samples.features.googleMap

import android.content.Context
import android.os.Bundle
import android.view.View
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.ktx.awaitMap
import com.skyyo.samples.R
import com.skyyo.samples.theme.mapStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GoogleMapScreen(viewModel: GoogleMapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle
    val mapBundle = rememberSaveable(key = "mapBundle") { Bundle() }
    val mapView = remember {
        MapView(context, GoogleMapOptions()).apply { // TODO .mapId(mapStyle) cloud based styles is missing in this version api
            id = View.generateViewId()
        }
    }
    val markers = remember(viewModel.markers, lifecycleOwner) {
        viewModel.markers.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }
    val events = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
    }

    LaunchedEffect(Unit) {
        val googleMap = mapView.awaitMap()
        mapStyle?.let { googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, it)) }
        val markerManager = MarkerManager(googleMap)
        val clusterManager = ClusterManager<ClusterItem>(context, googleMap, markerManager).apply {
            renderer = ClusterRender(context, googleMap, this)
            setOnClusterClickListener { cluster ->
                val positionBuilder = CameraPosition.Builder().apply {
                    target(cluster.position)
                    zoom(googleMap.cameraPosition.zoom + 1)
                    bearing(googleMap.cameraPosition.bearing)
                }.build()
                googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(positionBuilder), 300, null
                )
                true
            }
        }
        googleMap.apply {
            uiSettings.apply {
                isMyLocationButtonEnabled = false
                isMapToolbarEnabled = false
                isCompassEnabled = false
            }
            setOnCameraIdleListener(clusterManager)
            setOnMapClickListener {
                viewModel.removePolyline()
            }
        }
        launch { observePoints(markers, clusterManager) }
        launch { observeEvents(events, googleMap, context, markerManager) }
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
    lifecycleAwareEventsFlow: Flow<GoogleMapEvent>,
    googleMap: GoogleMap,
    context: Context,
    markerManager: MarkerManager
) {
    var finalPolyline: Polyline? = null
    lifecycleAwareEventsFlow.collect { event ->
        when (event) {
            is GoogleMapEvent.ShowKMLDraw -> {
                val layer = withContext(Dispatchers.Default) {
                    KmlLayer(
                        googleMap,
                        R.raw.map_layer,
                        context,
                        markerManager,
                        PolygonManager(googleMap),
                        PolylineManager(googleMap),
                        GroundOverlayManager(googleMap),
                        null
                    )
                }
                layer.addLayerToMap()
            }
            is GoogleMapEvent.ShowPolyline -> {
                finalPolyline = googleMap.addPolyline(event.options)
            }
            is GoogleMapEvent.RemovePolyline -> {
                finalPolyline?.remove()
            }
        }
    }
}
