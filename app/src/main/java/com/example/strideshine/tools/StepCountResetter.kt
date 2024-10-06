package com.example.strideshine.tools

import android.content.Context
import android.content.SharedPreferences

class StepCountResetter(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("step_tracker_prefs", Context.MODE_PRIVATE)

    private val resetInterval: Long = 24 * 60 * 60 * 1000 // 24 hours in milliseconds

    // Function to check if it's time to reset the step count

    fun checkAndResetStepCount(stepCount: Int): Int {
        val lastResetTime = getLastResetTime()
        val currentTime = System.currentTimeMillis()

        // If 24 hours have passed since last reset
        if (currentTime - lastResetTime >= resetInterval) {
            resetStepCount()
            return 0 // Reset step count
        }

        return stepCount // Return the current step count if not resetting
    }

    // Function to reset the step count and update last reset time
    private fun resetStepCount() {
        sharedPreferences.edit().putLong("last_reset_time", System.currentTimeMillis()).apply()
    }

    // Function to retrieve the last reset time
    private fun getLastResetTime(): Long {
        return sharedPreferences.getLong("last_reset_time", 0)
    }
}
