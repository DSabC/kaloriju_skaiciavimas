package com.example.kaloriju_skaiciavimas

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        renderSummary()

        findViewById<Button>(R.id.clearEntriesButton).setOnClickListener {
            AppStorage.clearFoodEntries(this)
            Toast.makeText(this, "Dienos įrašai išvalyti", Toast.LENGTH_SHORT).show()
            renderSummary()
        }

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun renderSummary() {
        val entries = AppStorage.getFoodEntries(this)
        val total = entries.sumOf { it.calories }
        val entriesContainer = findViewById<LinearLayout>(R.id.entriesContainer)

        findViewById<TextView>(R.id.totalCaloriesText).text = "Iš viso: $total kcal"
        entriesContainer.removeAllViews()

        if (entries.isEmpty()) {
            entriesContainer.addView(createEntryText("Šiandien dar nėra pridėtų maisto įrašų."))
            return
        }

        entries.forEach { entry ->
            entriesContainer.addView(
                createEntryText("${entry.name} • ${entry.amount} • ${entry.calories} kcal")
            )
        }
    }

    private fun createEntryText(text: String): TextView =
        TextView(this).apply {
            this.text = text
            textSize = 16f
            setTextColor(getColor(R.color.app_text))
            setPadding(0, 12, 0, 12)
        }
}
