package com.skyyo.igdbbrowser.features.samples.formValidation.realTime

sealed class FormValidationsRealTimeEvent {
    class ShowToast(val messageId: Int) : FormValidationsRealTimeEvent()
    class UpdateKeyboard(val show: Boolean) : FormValidationsRealTimeEvent()
}
