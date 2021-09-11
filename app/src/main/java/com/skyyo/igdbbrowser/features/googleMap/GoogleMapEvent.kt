package com.skyyo.igdbbrowser.features.googleMap

import com.google.android.gms.maps.model.PolylineOptions

sealed class GoogleMapEvent {
    object ShowKMLDraw : GoogleMapEvent()
    class ShowPolyline(val options: PolylineOptions) : GoogleMapEvent()
    object RemovePolyline : GoogleMapEvent()
}