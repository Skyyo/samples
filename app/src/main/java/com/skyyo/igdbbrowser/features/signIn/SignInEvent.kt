package com.skyyo.igdbbrowser.features.signIn

sealed class SignInEvent {
    object NetworkError : SignInEvent()
    //TODO Think of any case when this is better to model as event. So far sadly this is better to have
    // as a state value, though this might lead to shady states after PD restoration if saved in handle
    class UpdateLoadingIndicator(val isLoading: Boolean) : SignInEvent()
}
