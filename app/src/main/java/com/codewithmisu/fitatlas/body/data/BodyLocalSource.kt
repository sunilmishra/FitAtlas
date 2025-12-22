package com.codewithmisu.fitatlas.body.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyLocalSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(entities: List<BodyEntity>)

    @Query("SELECT * FROM table_body")
    suspend fun getAll(): List<BodyEntity>

    @Query("SELECT * FROM table_body")
    fun watchAll(): Flow<List<BodyEntity>>
}