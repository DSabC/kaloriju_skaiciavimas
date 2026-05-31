package com.example.kaloriju_skaiciavimas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GoalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        val goalInput = findViewById<EditText>(R.id.goalInput)
        goalInput.setText(AppStorage.getCalorieGoal(this).toString())

        findViewById<Button>(R.id.saveGoalButton).setOnClickListener {
            val goal = goalInput.text.toString().toIntOrNull()
            if (goal == null || goal <= 0) {
                Toast.makeText(this, "Įveskite teisingą kalorijų tikslą", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AppStorage.saveCalorieGoal(this, goal)
            Toast.makeText(this, "Tikslas išsaugotas", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            finish()
        }
    }
}
