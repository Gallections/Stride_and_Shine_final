package com.example.strideshine

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StatsActivity : AppCompatActivity() {

    private lateinit var dailyGoalButton: Button
    private lateinit var homeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        dailyGoalButton = findViewById(R.id.dailyGoalButton)
        homeButton = findViewById(R.id.homeButton)

        // Navigate to Daily Goal Activity
        dailyGoalButton.setOnClickListener {
            val intent = Intent(this, DailyGoalActivity::class.java)
            startActivity(intent)
        }

        // Navigate back to Main Activity
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
//            finish() // Optionally finish this activity}
            }
    }
}
