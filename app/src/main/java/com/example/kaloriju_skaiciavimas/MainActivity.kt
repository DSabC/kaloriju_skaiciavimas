package com.example.kaloriju_skaiciavimas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var greetingText: TextView
    private lateinit var goalText: TextView
    private lateinit var consumedText: TextView
    private lateinit var remainingText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        greetingText = findViewById(R.id.greetingText)
        goalText = findViewById(R.id.goalText)
        consumedText = findViewById(R.id.consumedText)
        remainingText = findViewById(R.id.remainingText)

        findViewById<Button>(R.id.addFoodButton).setOnClickListener {
            startActivity(Intent(this, AddFoodActivity::class.java))
        }

        findViewById<Button>(R.id.goalButton).setOnClickListener {
            startActivity(Intent(this, GoalActivity::class.java))
        }

        findViewById<Button>(R.id.summaryButton).setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            AppStorage.logout(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateDashboard()
    }

    private fun updateDashboard() {
        val user = AppStorage.getUser(this)
        val goal = AppStorage.getCalorieGoal(this)
        val consumed = AppStorage.getFoodEntries(this).sumOf { it.calories }
        val remaining = goal - consumed

        greetingText.text = "Sveiki, ${user?.name?.ifBlank { "naudotojau" } ?: "naudotojau"}"
        goalText.text = "Dienos tikslas: $goal kcal"
        consumedText.text = "Suvartota: $consumed kcal"
        remainingText.text = if (remaining >= 0) {
            "Liko: $remaining kcal"
        } else {
            "Viršyta: ${-remaining} kcal"
        }
    }
}
