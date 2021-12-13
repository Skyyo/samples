package com.skyyo.samples.application.persistance.room.assets

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AssetsRemoteKeysDao {


    @Query("SELECT * FROM AssetsRemoteKeys WHERE assetKey = :id")
    suspend fun remoteKeysById(id: Int): AssetsRemoteKeys?

    @Query("DELETE FROM AssetsRemoteKeys")
    suspend fun deleteRemoteKeys()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<AssetsRemoteKeys>)
}