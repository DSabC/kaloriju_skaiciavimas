package com.example.kaloriju_skaiciavimas

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object AppStorage {
    private const val PREFS_NAME = "calorie_counter_prefs"
    private const val KEY_USERS = "users"
    private const val KEY_CURRENT_EMAIL = "current_email"
    private const val KEY_LOGGED_IN = "logged_in"

    // JSON keys for User object
    private const val JSON_NAME = "name"
    private const val JSON_EMAIL = "email"
    private const val JSON_PASSWORD = "password"
    private const val JSON_GOAL = "calorie_goal"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun getAllUsers(context: Context): List<User> {
        val rawJson = prefs(context).getString(KEY_USERS, "[]") ?: "[]"
        val jsonArray = JSONArray(rawJson)
        val users = mutableListOf<User>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            users.add(User(
                userId = obj.optInt("user_id", obj.getString(JSON_EMAIL).hashCode()),
                name = obj.getString(JSON_NAME),
                email = obj.getString(JSON_EMAIL),
                password = obj.getString(JSON_PASSWORD),
                calorieGoal = obj.getInt(JSON_GOAL)
            ))
        }
        return users
    }

    private fun saveAllUsers(context: Context, users: List<User>) {
        val jsonArray = JSONArray()
        users.forEach { user ->
            jsonArray.put(JSONObject()
                .put("user_id", user.userId)
                .put(JSON_NAME, user.name)
                .put(JSON_EMAIL, user.email)
                .put(JSON_PASSWORD, user.password)
                .put(JSON_GOAL, user.calorieGoal)
            )
        }
        prefs(context).edit().putString(KEY_USERS, jsonArray.toString()).apply()
    }

    fun saveUser(context: Context, user: User) {
        val users = getAllUsers(context).toMutableList()
        users.removeAll { it.email == user.email }
        users.add(user)
        saveAllUsers(context, users)

        prefs(context).edit()
            .putString(KEY_CURRENT_EMAIL, user.email)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun getUser(context: Context): User? {
        val currentEmail = prefs(context).getString(KEY_CURRENT_EMAIL, null) ?: return null
        return getAllUsers(context).find { it.email == currentEmail }
    }

    fun login(context: Context, email: String, password: String): Boolean {
        val users = getAllUsers(context)
        val user = users.find { it.email == email && it.password == password }
        if (user != null) {
            prefs(context).edit()
                .putString(KEY_CURRENT_EMAIL, email)
                .putBoolean(KEY_LOGGED_IN, true)
                .apply()
            return true
        }
        return false
    }

    fun logout(context: Context) {
        prefs(context).edit()
            .putBoolean(KEY_LOGGED_IN, false)
            .remove(KEY_CURRENT_EMAIL)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_LOGGED_IN, false)

    fun userExists(context: Context, email: String): Boolean {
        return getAllUsers(context).any { it.email == email }
    }

    fun saveCalorieGoal(context: Context, goal: Int) {
        val currentEmail = prefs(context).getString(KEY_CURRENT_EMAIL, null) ?: return
        val users = getAllUsers(context).toMutableList()
        val index = users.indexOfFirst { it.email == currentEmail }
        if (index != -1) {
            val updatedUser = users[index].copy(calorieGoal = goal)
            users[index] = updatedUser
            saveAllUsers(context, users)
        }
    }

    fun getCalorieGoal(context: Context): Int {
        return getUser(context)?.calorieGoal ?: 2000
    }

    private fun getFoodEntriesKey(context: Context): String {
        val email = prefs(context).getString(KEY_CURRENT_EMAIL, "global") ?: "global"
        return "food_entries_$email"
    }

    private fun getProductsKey(context: Context): String {
        val email = prefs(context).getString(KEY_CURRENT_EMAIL, "global") ?: "global"
        return "products_$email"
    }

    fun addFoodEntry(context: Context, entry: FoodEntry) {
        val entries = getFoodEntries(context).toMutableList()
        entries.add(entry)
        saveFoodEntries(context, entries)
    }

    fun saveProduct(context: Context, product: Product) {
        val products = getProducts(context).toMutableList()
        products.removeAll { it.name.equals(product.name, ignoreCase = true) }
        products.add(product)
        saveProducts(context, products.sortedBy { it.name.lowercase() })
    }

    fun getProducts(context: Context): List<Product> {
        val rawJson = prefs(context).getString(getProductsKey(context), "[]") ?: "[]"
        val jsonArray = JSONArray(rawJson)
        val products = mutableListOf<Product>()

        for (index in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(index)
            products.add(
                Product(
                    productId = item.optInt("product_id", item.getString("name").hashCode()),
                    name = item.getString("name"),
                    caloriesPer100g = item.getDouble("calories_per_100g")
                )
            )
        }

        return products
    }

    fun getFoodEntries(context: Context): List<FoodEntry> {
        val key = getFoodEntriesKey(context)
        val rawJson = prefs(context).getString(key, "[]") ?: "[]"
        val jsonArray = JSONArray(rawJson)
        val entries = mutableListOf<FoodEntry>()

        for (index in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(index)
            entries.add(
                FoodEntry(
                    entryId = item.optInt("entry_id", index + 1),
                    name = item.getString("name"),
                    caloriesPer100g = item.optDouble(
                        "calories_per_100g",
                        item.optDouble("calories", 0.0)
                    ),
                    amountGrams = item.optDouble(
                        "amount_grams",
                        item.optString("amount", "100").toDoubleOrNull() ?: 100.0
                    ),
                    calculatedCalories = item.optDouble(
                        "calculated_calories",
                        item.optDouble("calories", 0.0)
                    ),
                    date = item.optString("date", "")
                )
            )
        }
        return entries
    }

    fun clearFoodEntries(context: Context) {
        saveFoodEntries(context, emptyList())
    }

    private fun saveFoodEntries(context: Context, entries: List<FoodEntry>) {
        val key = getFoodEntriesKey(context)
        val jsonArray = JSONArray()
        entries.forEach { entry ->
            jsonArray.put(
                JSONObject()
                    .put("entry_id", entry.entryId)
                    .put("name", entry.name)
                    .put("calories_per_100g", entry.caloriesPer100g)
                    .put("amount_grams", entry.amountGrams)
                    .put("calculated_calories", entry.calculatedCalories)
                    .put("date", entry.date)
            )
        }
        prefs(context).edit().putString(key, jsonArray.toString()).apply()
    }

    private fun saveProducts(context: Context, products: List<Product>) {
        val jsonArray = JSONArray()
        products.forEach { product ->
            jsonArray.put(
                JSONObject()
                    .put("product_id", product.productId)
                    .put("name", product.name)
                    .put("calories_per_100g", product.caloriesPer100g)
            )
        }
        prefs(context).edit().putString(getProductsKey(context), jsonArray.toString()).apply()
    }
}
