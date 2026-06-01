package com.example.kaloriju_skaiciavimas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey
    val entryId: Int = 0,
    val userEmail: String = "",
    val name: String,
    val caloriesPer100g: Double,
    val amountGrams: Double,
    val calculatedCalories: Double,
    val date: String
)
