package com.example.kaloriju_skaiciavimas

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddFoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        val foodNameInput = findViewById<AutoCompleteTextView>(R.id.foodNameInput)
        val caloriesInput = findViewById<EditText>(R.id.caloriesInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)
        val calculatedCaloriesText = findViewById<TextView>(R.id.calculatedCaloriesText)
        val savedProducts = AppStorage.getProducts(this)

        val productAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            savedProducts.map { it.name }
        )
        foodNameInput.setAdapter(productAdapter)
        foodNameInput.threshold = 0

        foodNameInput.setOnClickListener {
            foodNameInput.showDropDown()
        }

        foodNameInput.setOnItemClickListener { _, _, position, _ ->
            val selectedName = productAdapter.getItem(position) ?: return@setOnItemClickListener
            val product = savedProducts.find { it.name == selectedName } ?: return@setOnItemClickListener
            caloriesInput.setText(formatNumber(product.caloriesPer100g))
            updateCalculatedCalories(caloriesInput, amountInput, calculatedCaloriesText)
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCalculatedCalories(caloriesInput, amountInput, calculatedCaloriesText)
            }
            override fun afterTextChanged(s: Editable?) = Unit
        }
        caloriesInput.addTextChangedListener(watcher)
        amountInput.addTextChangedListener(watcher)

        findViewById<Button>(R.id.saveFoodButton).setOnClickListener {
            val name = foodNameInput.text.toString().trim()
            val caloriesPer100g = caloriesInput.text.toString().toDoubleOrNull()
            val amountGrams = amountInput.text.toString().toDoubleOrNull()

            if (name.isBlank() || caloriesPer100g == null || caloriesPer100g <= 0 || amountGrams == null || amountGrams <= 0) {
                Toast.makeText(this, "Teisingai užpildykite visus laukus", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calculatedCalories = calculateCalories(caloriesPer100g, amountGrams)
            AppStorage.saveProduct(
                this,
                Product(
                    productId = name.lowercase().hashCode(),
                    name = name,
                    caloriesPer100g = caloriesPer100g
                )
            )
            AppStorage.addFoodEntry(
                this,
                FoodEntry(
                    entryId = System.currentTimeMillis().toInt(),
                    name = name,
                    caloriesPer100g = caloriesPer100g,
                    amountGrams = amountGrams,
                    calculatedCalories = calculatedCalories,
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
            )
            Toast.makeText(this, "Maistas pridėtas", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            finish()
        }
    }

    private fun updateCalculatedCalories(
        caloriesInput: EditText,
        amountInput: EditText,
        calculatedCaloriesText: TextView
    ) {
        val caloriesPer100g = caloriesInput.text.toString().toDoubleOrNull()
        val amountGrams = amountInput.text.toString().toDoubleOrNull()
        calculatedCaloriesText.text = if (caloriesPer100g != null && amountGrams != null) {
            "Apskaičiuotos kalorijos: ${formatNumber(calculateCalories(caloriesPer100g, amountGrams))} kcal"
        } else {
            "Apskaičiuotos kalorijos: 0 kcal"
        }
    }

    private fun calculateCalories(caloriesPer100g: Double, amountGrams: Double): Double =
        caloriesPer100g * amountGrams / 100.0

    private fun formatNumber(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString() else String.format(Locale.getDefault(), "%.1f", value)
}
