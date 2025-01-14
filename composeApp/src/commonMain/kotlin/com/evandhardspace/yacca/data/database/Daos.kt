package com.evandhardspace.yacca.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: CurrencyEntity)

    @Query("SELECT * FROM favorite_currencies")
    fun getAll(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM favorite_currencies WHERE id = :id")
    suspend fun getById(id: String): CurrencyEntity?

    @Delete
    suspend fun delete(currency: CurrencyEntity)
}