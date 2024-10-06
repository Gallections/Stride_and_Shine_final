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

data class StepData(
    val date: String,
    val stepCount: Int
)

class StepCountWriter(private val context: Context) {

    private val gson = Gson()
    private val stepDataFile = File(context.filesDir, "com.example.strideshine/data/daily_steps.json")

    // Function to save the daily step count
    fun saveDailyStepCount(stepCount: Int) {
        val currentDate = getCurrentDate()
        val existingData = loadAllStepData().toMutableList()

        // Check if there is already an entry for today
        val todayEntryIndex = existingData.indexOfFirst { it.date == currentDate }
        if (todayEntryIndex != -1) {
            // If today's entry exists, update the step count
            existingData[todayEntryIndex] = existingData[todayEntryIndex].copy(stepCount = stepCount)
        } else {
            // If no entry exists for today, add a new entry
            existingData.add(StepData(currentDate, stepCount))
        }

        // Save all the step data back to the file
        saveAllStepData(existingData)
    }

    // Function to load all past user data
    fun loadAllStepData(): List<StepData> {
        return if (stepDataFile.exists()) {
            FileReader(stepDataFile).use { reader ->
                val type = object : TypeToken<List<StepData>>() {}.type
                gson.fromJson(reader, type) ?: emptyList()
            }
        } else {
            emptyList()
        }
    }

    // Function to save the entire list of step data to the file
    private fun saveAllStepData(stepDataList: List<StepData>) {
        FileWriter(stepDataFile).use { writer ->
            gson.toJson(stepDataList, writer)
        }
    }

    // Function to get the current date in a specific format
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
