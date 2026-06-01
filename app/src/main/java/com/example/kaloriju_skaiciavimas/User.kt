package com.example.kaloriju_skaiciavimas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    val userId: Int = 0,
    val name: String,
    @PrimaryKey
    val email: String,
    val password: String,
    val calorieGoal: Int = 2000
)
