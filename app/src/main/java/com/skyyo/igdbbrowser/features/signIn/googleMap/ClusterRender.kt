package com.skyyo.igdbbrowser.features.signIn.googleMap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.BitmapDescriptor
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.skyyo.igdbbrowser.R

class ClusterRender(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<ClusterItem>?
) : DefaultClusterRenderer<ClusterItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: ClusterItem, markerOptions: MarkerOptions) {
        getDefaultMarker(context)
    }
}

fun getDefaultMarker(context: Context): BitmapDescriptor? {
    return ContextCompat.getDrawable(context, R.drawable.maps_default_marker)?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            intrinsicWidth,
            intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        draw(Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}