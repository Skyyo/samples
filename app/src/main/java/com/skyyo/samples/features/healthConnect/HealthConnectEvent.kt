package com.skyyo.samples.features.healthConnect

sealed class HealthConnectEvent {
    class PermissionsStatus(val areAllPermissionsGranted: Boolean): HealthConnectEvent()
}