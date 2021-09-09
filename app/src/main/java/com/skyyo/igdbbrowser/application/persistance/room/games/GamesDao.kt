package com.skyyo.igdbbrowser.application.persistance.room.games

import androidx.paging.PagingSource
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

    @Query("SELECT * from games_table")
    fun observeGamesPaging(): PagingSource<Int, Game>

    @Query("SELECT * FROM games_table")
    fun pagingSource(): PagingSource<Int, Game>

    @Query("DELETE from games_table")
    suspend fun deleteGames()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<Game>)

    @Query("DELETE FROM games_table")
    suspend fun clearAll()

}