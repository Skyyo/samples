package com.skyyo.samples.application.persistance.room.cats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CatsRemoteKeysDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<CatsRemoteKeys>)

    @Query("SELECT * FROM cats_remote_keys WHERE catId= :id")
    suspend fun remoteKeysCatId(id: Int): CatsRemoteKeys?

    @Query("DELETE FROM cats_remote_keys")
    suspend fun deleteRemoteKeys()

}