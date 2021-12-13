package com.skyyo.samples.application.persistance.room.assets

import androidx.paging.PagingSource
import androidx.room.*
import com.skyyo.samples.application.models.Asset

@Dao
interface AssetsDao {

    @Query("SELECT * FROM Asset WHERE id = :id")
    suspend fun getAssetById(id: String): Asset

    @Query("SELECT * FROM Asset")
    fun getPagingSource(): PagingSource<Int, Asset>

    @Query("DELETE FROM Asset")
    suspend fun deleteAssets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assets: List<Asset>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAsset(asset: Asset)
}