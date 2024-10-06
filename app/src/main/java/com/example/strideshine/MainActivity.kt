package com.example.strideshine

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Button
import android.widget.Toast
import com.example.strideshine.tools.StepCountResetter
import com.example.strideshine.tools.StepCountWriter
import com.example.strideshine.tools.DailyGoal


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private lateinit var stepCountTextView: TextView
    private var stepCount: Int = 0
    private var isFirstReading = true
    private var initialStepCount: Int = 0
    private lateinit var stepCountResetter: StepCountResetter
    private lateinit var stepCountWriter: StepCountWriter
    private lateinit var dailyGoal: DailyGoal
    private lateinit var dailyGoalButton: Button
    private lateinit var statsButton: Button
    private lateinit var stepNotification: StepNotification
    private lateinit var quoteNotification: QuoteNotification

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // When the button on daily goal is clicked
        dailyGoalButton = findViewById(R.id.dailyGoalButton)
        statsButton = findViewById(R.id.statsButton)
        dailyGoalButton.setOnClickListener {
            // Navigate to the DailyGoalActivity
            val intent = Intent(this, DailyGoalActivity::class.java)
            startActivity(intent)
        }

        statsButton.setOnClickListener {
            // Handle stats button logic here
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        stepCountTextView = findViewById(R.id.stepCountTextView)

        // Check and request permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1)
        }

        // Initialize notification and sensor components
        stepNotification = StepNotification(this)
        stepNotification.createNotificationChannel()
        stepNotification.requestNotificationPermission(this)

        // Initialize Sensor Manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepCounterSensor == null) {
            stepCountTextView.text = "Step Counter Sensor not available"
        }

        stepCountResetter = StepCountResetter(this)
        stepCountWriter = StepCountWriter(this)
        dailyGoal = DailyGoal(this)

        // Show immediate quote notification on app open
        quoteNotification = QuoteNotification(this)
        quoteNotification.showRandomQuoteNotification()

        // Reset step count if needed and load the current day's steps
        stepCount = stepCountResetter.checkAndResetStepCount(stepCount)
        stepCount = stepCountWriter.loadAllStepData()
            .find { it.date == stepCountWriter.getCurrentDate() }?.stepCount ?: 0

        // Set initial step count for daily tracking
        initialStepCount = stepCount
    }

    override fun onResume() {
        super.onResume()
        stepCounterSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            // The first value returned by the step counter is the total number of steps since the device was booted.
            stepCount = event.values[0].toInt()
            stepCountTextView.text = "Steps: $stepCount"
            onStepDetectedReset()

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // You can handle sensor accuracy changes if needed
    }

    // Handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
                stepCountTextView.text = "Permission denied to access step count"
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // Detects step changes and checks if it's time to reset
    private fun onStepDetectedReset() {
        // Check and reset step count if necessary
        stepCount = stepCountResetter.checkAndResetStepCount(stepCount)
        // Update the UI with the current step count
        stepCountTextView.text = stepCount.toString()
        stepCountWriter.saveDailyStepCount(stepCount)
    }

}

