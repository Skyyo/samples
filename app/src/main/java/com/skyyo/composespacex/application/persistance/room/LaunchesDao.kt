package com.skyyo.composespacex.application.persistance.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skyyo.composespacex.application.models.remote.Launch
import kotlinx.coroutines.flow.Flow

@Dao
interface LaunchesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLatestLaunch(latestLaunch: Launch)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPastLaunches(upcomingLaunches: List<Launch>)

    @Query("SELECT * from launches_table  WHERE upcoming = 0 AND launchDate <= :currentDate LIMIT 1")
    fun observeLatestLaunch(currentDate: String): Flow<Launch>

    @Query("SELECT * from launches_table")
    fun observeLatestLaunches(): Flow<List<Launch>>

//    @Query("SELECT * from launches_table WHERE flightNumber IN (:ids)")
//    suspend fun getUpcomingLaunchesById(ids: List<Int>): List<Launch>

//    @Query("DELETE from launches_table")
//    suspend fun clearUpcomingLaunches()
}