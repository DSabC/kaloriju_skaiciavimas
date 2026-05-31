package com.example.kaloriju_skaiciavimas

data class User(
    val userId: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val calorieGoal: Int = 2000
)
