package com.skyyo.samples.application.persistance.room.cats

import androidx.paging.PagingSource
import androidx.room.*
import com.skyyo.samples.application.models.Cat
import kotlinx.coroutines.flow.Flow

@Dao
interface CatsDao {

    @Transaction
    suspend fun deleteAndInsertCats(cats: List<Cat>) {
        deleteCats()
        insertCats(cats)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCats(cats: List<Cat>)

    @Query("SELECT * from cats_table")
    fun observeCats(): Flow<List<Cat>>

    @Query("SELECT * from cats_table")
    fun observeCatsPaging(): PagingSource<Int, Cat>

    @Query("SELECT * FROM cats_table")
    fun pagingSource(): PagingSource<Int, Cat>

    @Query("DELETE from cats_table")
    suspend fun deleteCats()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cats: List<Cat>)

    @Query("DELETE FROM cats_table")
    suspend fun clearAll()

}