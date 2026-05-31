package com.example.kaloriju_skaiciavimas

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object AppStorage {
    private const val PREFS_NAME = "calorie_counter_prefs"
    private const val KEY_NAME = "name"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_CALORIE_GOAL = "calorie_goal"
    private const val KEY_FOOD_ENTRIES = "food_entries"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUser(context: Context, user: User) {
        prefs(context).edit()
            .putString(KEY_NAME, user.name)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_PASSWORD, user.password)
            .putInt(KEY_CALORIE_GOAL, user.calorieGoal)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun getUser(context: Context): User? {
        val preferences = prefs(context)
        val email = preferences.getString(KEY_EMAIL, null) ?: return null
        val name = preferences.getString(KEY_NAME, "") ?: ""
        val password = preferences.getString(KEY_PASSWORD, "") ?: ""
        val goal = preferences.getInt(KEY_CALORIE_GOAL, 2000)
        return User(name = name, email = email, password = password, calorieGoal = goal)
    }

    fun login(context: Context, email: String, password: String): Boolean {
        val user = getUser(context) ?: return false
        val success = user.email == email && user.password == password
        if (success) {
            prefs(context).edit().putBoolean(KEY_LOGGED_IN, true).apply()
        }
        return success
    }

    fun logout(context: Context) {
        prefs(context).edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_LOGGED_IN, false)

    fun saveCalorieGoal(context: Context, goal: Int) {
        prefs(context).edit().putInt(KEY_CALORIE_GOAL, goal).apply()
    }

    fun getCalorieGoal(context: Context): Int =
        prefs(context).getInt(KEY_CALORIE_GOAL, 2000)

    fun addFoodEntry(context: Context, entry: FoodEntry) {
        val entries = getFoodEntries(context).toMutableList()
        entries.add(entry)
        saveFoodEntries(context, entries)
    }

    fun getFoodEntries(context: Context): List<FoodEntry> {
        val rawJson = prefs(context).getString(KEY_FOOD_ENTRIES, "[]") ?: "[]"
        val jsonArray = JSONArray(rawJson)
        val entries = mutableListOf<FoodEntry>()

        for (index in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(index)
            entries.add(
                FoodEntry(
                    name = item.getString("name"),
                    calories = item.getInt("calories"),
                    amount = item.getString("amount")
                )
            )
        }

        return entries
    }

    fun clearFoodEntries(context: Context) {
        saveFoodEntries(context, emptyList())
    }

    private fun saveFoodEntries(context: Context, entries: List<FoodEntry>) {
        val jsonArray = JSONArray()
        entries.forEach { entry ->
            jsonArray.put(
                JSONObject()
                    .put("name", entry.name)
                    .put("calories", entry.calories)
                    .put("amount", entry.amount)
            )
        }
        prefs(context).edit().putString(KEY_FOOD_ENTRIES, jsonArray.toString()).apply()
    }
}
