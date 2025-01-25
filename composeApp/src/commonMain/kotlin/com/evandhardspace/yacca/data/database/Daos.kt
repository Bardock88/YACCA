package com.evandhardspace.yacca.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: CurrencyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<CurrencyEntity>)

    @Query("SELECT * FROM currencies")
    fun getAll(): Flow<List<CurrencyEntity>>

    @Query("UPDATE currencies SET isFavourite = :isFavourite WHERE id = :id")
    suspend fun updateFavourite(id: String, isFavourite: Boolean)

    @Query("DELETE FROM currencies WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM currencies")
    suspend fun clearAll()
}