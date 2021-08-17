package com.skyyo.igdbbrowser.application.persistance.room

import androidx.room.*
import com.skyyo.igdbbrowser.application.models.remote.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GamesDao {

    @Transaction
    suspend fun deleteAndInsertGames(games: List<Game>) {
        deleteGames()
        insertGames(games)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<Game>)

    @Query("SELECT * from games_table")
    fun observeGames(): Flow<List<Game>>

    @Query("DELETE from games_table")
    suspend fun deleteGames()

//    @Query("SELECT * from launches_table WHERE flightNumber IN (:ids)")
//    suspend fun getUpcomingLaunchesById(ids: List<Int>): List<Launch>

    //    @Query("SELECT * from launches_table  WHERE upcoming = 0 AND launchDate <= :currentDate LIMIT 1")
//    fun observeLatestLaunch(currentDate: String): Flow<Launch>


}