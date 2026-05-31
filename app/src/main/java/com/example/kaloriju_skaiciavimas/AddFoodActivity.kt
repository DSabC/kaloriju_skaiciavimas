package com.example.kaloriju_skaiciavimas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddFoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        val foodNameInput = findViewById<EditText>(R.id.foodNameInput)
        val caloriesInput = findViewById<EditText>(R.id.caloriesInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)

        findViewById<Button>(R.id.saveFoodButton).setOnClickListener {
            val name = foodNameInput.text.toString().trim()
            val calories = caloriesInput.text.toString().toIntOrNull()
            val amount = amountInput.text.toString().trim()

            if (name.isBlank() || calories == null || calories <= 0 || amount.isBlank()) {
                Toast.makeText(this, "Teisingai užpildykite visus laukus", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AppStorage.addFoodEntry(this, FoodEntry(name = name, calories = calories, amount = amount))
            Toast.makeText(this, "Maistas pridėtas", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            finish()
        }
    }
}
