package com.skyyo.samples.features.googleMap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.skyyo.samples.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GoogleMapViewModel @Inject constructor() : ViewModel() {

    private val _markers = MutableStateFlow<List<Cluster>?>(null)
    val markers = _markers.filterNotNull()
    private val _events = Channel<GoogleMapEvent>(Channel.UNLIMITED)
    val events = _events.receiveAsFlow()

    init {
        getFakeLocations()
        showKMLData()
        showPolyline()
    }

    fun removePolyline() {
        _events.trySend(GoogleMapEvent.RemovePolyline)
    }

    private fun getFakeLocations() = viewModelScope.launch(Dispatchers.Default) {
        val pointsList = mutableListOf<Cluster>()
        repeat(100) {
            val lat = it * 0.4
            val lng = it * 0.4
            pointsList.add(Cluster(lat, lng))
        }
        _markers.value = pointsList
    }

    private fun showKMLData() = viewModelScope.launch {
        _events.send(GoogleMapEvent.ShowKMLDraw)
    }

    private fun showPolyline() {
        val polylineOptions = PolylineOptions().apply {
            color(R.color.teal_200)
            addAll(mockedList)
        }
        _events.trySend(GoogleMapEvent.ShowPolyline(polylineOptions))
    }

    companion object {
        private val mockedList = listOf(
            LatLng(50.458562, 30.414559),
            LatLng(50.247033, 28.693214),
            LatLng(49.229649, 28.410986),
            LatLng(49.384039, 27.009104),
            LatLng(49.810147, 24.099726),
        )
    }
}