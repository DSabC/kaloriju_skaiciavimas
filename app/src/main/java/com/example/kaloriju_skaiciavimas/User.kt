package com.example.kaloriju_skaiciavimas

data class User(
    val name: String,
    val email: String,
    val password: String,
    val calorieGoal: Int = 2000
)
