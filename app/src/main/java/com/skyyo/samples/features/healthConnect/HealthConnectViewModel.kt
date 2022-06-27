package com.skyyo.samples.features.healthConnect

import android.app.Application
import android.os.RemoteException
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.extensions.getStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    val permissions = setOf(
        Permission.createReadPermission(Steps::class),
        Permission.createWritePermission(Steps::class),
        Permission.createReadPermission(ActivitySession::class)
    )
    private val lastWrittenRecordUid = handle.getStateFlow<String?>(viewModelScope, "lastWrittenRecordUid", null)
    val stepsWritten = handle.getStateFlow(viewModelScope, "stepsWritten", 1L)
    val stepsRead = handle.getStateFlow<Long?>(viewModelScope, "stepsRead", null)
    val localStepsCanBeRead = lastWrittenRecordUid.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)
    val accumulated3rdPartySteps = handle.getStateFlow(viewModelScope, "3rdPartySteps", 0L)
    val areAllPermissionsGranted = handle.getStateFlow(viewModelScope, "areAllPermissionsGranted", false)

    suspend fun checkPermissions() {
        areAllPermissionsGranted.value = healthConnectClient.hasAllPermissions(permissions)
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

    fun read3rdPartySteps(activitySessionUid: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val activitySession =
                healthConnectClient.readRecord(ActivitySession::class, activitySessionUid)
            // Use the start time and end time from the session, for reading raw and aggregate data.
            val timeRangeFilter = TimeRangeFilter.between(
                startTime = activitySession.record.startTime,
                endTime = activitySession.record.endTime
            )
            val aggregateDataTypes = setOf(Steps.COUNT_TOTAL)
            // Limit the data read to just the application that wrote the session. This may or may not
            // be desirable depending on the use case: In some cases, it may be useful to combine with
            // data written by other apps.
            val dataOriginFilter = listOf(activitySession.record.metadata.dataOrigin)
            val aggregateRequest = AggregateRequest(
                metrics = aggregateDataTypes,
                timeRangeFilter = timeRangeFilter,
                dataOriginFilter = dataOriginFilter
            )
            val aggregateData = healthConnectClient.aggregate(aggregateRequest)
            accumulated3rdPartySteps.value = aggregateData[Steps.COUNT_TOTAL] ?: 0L
        } catch (e: IllegalArgumentException) {
            //activity session uid is empty or null
            accumulated3rdPartySteps.value = 0L
        } catch (e: RemoteException) {
            //activity session uid not exists
            accumulated3rdPartySteps.value = 0L
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