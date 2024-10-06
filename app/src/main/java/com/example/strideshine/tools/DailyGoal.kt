package com.example.strideshine.tools

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DailyStepGoal(
    val date: String,
    val stepGoal: Int
)

class DailyGoal(private val context: Context) {
    private val gson = Gson()
    private val goalFile = File(context.filesDir, "com.example.strideshine/data/daily_goal.json")

    // Function to set a daily goal for steps
    fun setDailyGoal(stepGoal: Int) {
        val currentDate = getCurrentDate()
        val existingGoals = loadAllDailyGoals().toMutableList()

        // Check if there is already a goal set for today
        val todayGoalIndex = existingGoals.indexOfFirst { it.date == currentDate }
        if (todayGoalIndex != -1) {
            // If a goal for today exists, update the goal
            existingGoals[todayGoalIndex] = existingGoals[todayGoalIndex].copy(stepGoal = stepGoal)
        } else {
            // If no goal exists for today, add a new goal
            existingGoals.add(DailyStepGoal(currentDate, stepGoal))
        }

        // Save all goals back to the file
        saveAllDailyGoals(existingGoals)
    }

    // Function to load all past daily goals
    fun loadAllDailyGoals(): List<DailyStepGoal> {
        return if (goalFile.exists()) {
            FileReader(goalFile).use { reader ->
                val type = object : TypeToken<List<DailyStepGoal>>() {}.type
                gson.fromJson(reader, type) ?: emptyList()
            }
        } else {
            emptyList()
        }
    }

    // Function to save the entire list of daily goals to the file
    private fun saveAllDailyGoals(goalList: List<DailyStepGoal>) {
        FileWriter(goalFile).use { writer ->
            gson.toJson(goalList, writer)
        }
    }

    // Function to get the current date in a specific format
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // Function to retrieve today's step goal
    fun getTodaysGoal(): Int? {
        val currentDate = getCurrentDate()
        val goals = loadAllDailyGoals()
        return goals.find { it.date == currentDate }?.stepGoal
    }

    // Function to check if the daily step count meets the goal
    fun hasMetGoal(currentStepCount: Int): Boolean {
        val stepGoal = getTodaysGoal()
        return stepGoal != null && currentStepCount >= stepGoal
    }
}
