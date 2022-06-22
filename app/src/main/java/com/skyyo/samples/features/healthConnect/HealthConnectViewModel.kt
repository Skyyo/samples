package com.skyyo.samples.features.healthConnect

import android.app.Application
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.extensions.getStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HealthConnectViewModel @Inject constructor(
    handle: SavedStateHandle,
    val application: Application,
): ViewModel() {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(application.applicationContext) }
    val events = Channel<HealthConnectEvent>(Channel.UNLIMITED)
    val permissions = setOf(
        Permission.createReadPermission(Steps::class),
        Permission.createWritePermission(Steps::class)
    )
    private val lastWrittenRecordUid = handle.getStateFlow<String?>(viewModelScope, "lastWrittenRecordUid", null)
    val stepsWritten = handle.getStateFlow(viewModelScope, "stepsWritten", 1L)
    val stepsRead = handle.getStateFlow<Long?>(viewModelScope, "stepsRead", null)
    val stepsCanBeRead = lastWrittenRecordUid.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    suspend fun checkPermissions() {
        val areAllPermissionsGranted = healthConnectClient.hasAllPermissions(permissions)
        events.send(HealthConnectEvent.PermissionsStatus(areAllPermissionsGranted))
    }

    /**
     * Determines whether all the specified permissions are already granted. It is recommended to
     * call [PermissionController.getGrantedPermissions] first in the permissions flow, as if the
     * permissions are already granted then there is no need to request permissions via
     * [HealthDataRequestPermissions].
     */
    private suspend fun HealthConnectClient.hasAllPermissions(permissions: Set<Permission>): Boolean {
        return permissions == permissionController.getGrantedPermissions(
            permissions
        )
    }

    fun writeSteps() = viewModelScope.launch(Dispatchers.IO) {
        lastWrittenRecordUid.value = healthConnectClient.writeSteps(stepsWritten.value)
        stepsWritten.value++
    }

    fun readSteps() = viewModelScope.launch(Dispatchers.IO) {
        val recordUid = lastWrittenRecordUid.value
        if (recordUid != null) {
            stepsRead.value = healthConnectClient.readSteps(recordUid)
        }
    }

    private suspend fun HealthConnectClient.writeSteps(count: Long): String {
        val records = mutableListOf<Record>()
        val now = ZonedDateTime.now()
        records.add(
            Steps(
                count = count,
                startTime = now.minusHours(2).toInstant(),
                startZoneOffset = now.offset,
                endTime = now.toInstant(),
                endZoneOffset = now.offset
            )
        )
        return insertRecords(records).recordUidsList[0]
    }

    private suspend fun HealthConnectClient.readSteps(recordUid: String): Long {
        return readRecord(Steps::class, recordUid).record.count
    }
}