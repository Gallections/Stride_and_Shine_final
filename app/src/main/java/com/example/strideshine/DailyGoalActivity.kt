package com.example.strideshine

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DailyGoalActivity : AppCompatActivity() {

    private lateinit var dailyGoalInput: EditText
    private lateinit var setGoalButton: Button
    private lateinit var dailyGoalText: TextView
    private lateinit var statsButton : Button
    private lateinit var homeButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_goal)

        dailyGoalInput = findViewById(R.id.dailyGoalInput)
        setGoalButton = findViewById(R.id.setGoalButton)
        dailyGoalText = findViewById(R.id.dailyGoalText)
        homeButton = findViewById(R.id.homeButton)
        statsButton = findViewById(R.id.statsButton)

        setGoalButton.setOnClickListener {
            val dailyGoal = dailyGoalInput.text.toString()
            if (dailyGoal.isNotEmpty()) {
                // Set the text to display the daily goal
                dailyGoalText.text = "Your daily goal is $dailyGoal steps."

                // Hide the input layout and show the goal text
                setGoalButton.visibility = View.GONE
                dailyGoalInput.visibility = View.GONE
                dailyGoalText.visibility = View.VISIBLE
            }
        }

        // Navigate back to the main screen (Home button)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Navigate to stats (replace this with stats activity if you have one)
        statsButton.setOnClickListener {
            // Handle stats button logic here
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }
    }
}
