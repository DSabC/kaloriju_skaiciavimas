package com.example.kaloriju_skaiciavimas

data class FoodEntry(
    val entryId: Int = 0,
    val name: String,
    val caloriesPer100g: Double,
    val amountGrams: Double,
    val calculatedCalories: Double,
    val date: String
)
