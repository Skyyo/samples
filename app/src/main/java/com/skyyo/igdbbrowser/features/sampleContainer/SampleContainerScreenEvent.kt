package com.skyyo.igdbbrowser.features.sampleContainer

sealed class SampleContainerScreenEvent {
    object NetworkError : SampleContainerScreenEvent()
    class UpdateLoadingIndicator(val isLoading: Boolean) : SampleContainerScreenEvent()
}
