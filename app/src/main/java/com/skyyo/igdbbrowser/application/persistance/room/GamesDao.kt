package com.skyyo.igdbbrowser.application.persistance.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.models.remote.Launch
import kotlinx.coroutines.flow.Flow

@Dao
interface GamesDao {

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertLatestLaunch(latestLaunch: Launch)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(upcomingLaunches: List<Game>)

//    @Query("SELECT * from launches_table  WHERE upcoming = 0 AND launchDate <= :currentDate LIMIT 1")
//    fun observeLatestLaunch(currentDate: String): Flow<Launch>

    @Query("SELECT * from games_table")
    fun observeGames(): Flow<List<Game>>

//    @Query("SELECT * from launches_table WHERE flightNumber IN (:ids)")
//    suspend fun getUpcomingLaunchesById(ids: List<Int>): List<Launch>

//    @Query("DELETE from launches_table")
//    suspend fun clearUpcomingLaunches()
}