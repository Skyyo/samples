package com.skyyo.igdbbrowser.features.samples.googleMap

import com.google.android.libraries.maps.model.PolylineOptions

sealed class GoogleMapEvent {
    object ShowKMLDraw : GoogleMapEvent()
    class ShowPolyline(val options: PolylineOptions) : GoogleMapEvent()
    object RemovePolyline : GoogleMapEvent()
}