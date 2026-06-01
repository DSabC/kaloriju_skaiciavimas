package com.example.kaloriju_skaiciavimas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val productId: Int = 0,
    val name: String,
    val caloriesPer100g: Double
)
