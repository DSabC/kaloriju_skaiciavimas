package com.example.kaloriju_skaiciavimas

import androidx.room.*

@Dao
interface FoodEntryDao {
    @Query("SELECT * FROM food_entries WHERE userEmail = :email")
    suspend fun getEntriesForUser(email: String): List<FoodEntry>

    @Insert
    suspend fun insertEntry(entry: FoodEntry)

    @Query("DELETE FROM food_entries WHERE userEmail = :email")
    suspend fun clearEntriesForUser(email: String)
}
