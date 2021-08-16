package com.skyyo.igdbbrowser.features.signIn.googleMap

import com.google.android.libraries.maps.model.PolylineOptions

sealed class MapEvent {
    object ShowKMLDraw : MapEvent()
    class ShowPolyline(val options: PolylineOptions) : MapEvent()
    object RemovePolyline : MapEvent()
}