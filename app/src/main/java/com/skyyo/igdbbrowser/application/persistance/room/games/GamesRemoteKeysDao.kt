package com.skyyo.igdbbrowser.application.persistance.room.games

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GamesRemoteKeysDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<GamesRemoteKeys>)

    @Query("SELECT * FROM games_remote_keys WHERE gameId= :id")
    suspend fun remoteKeysGameId(id: Int): GamesRemoteKeys?

    @Query("DELETE FROM games_remote_keys")
    suspend fun deleteRemoteKeys()

}