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

        // Initialize sensor manager
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

        initializeSensor()
    }

    private fun initializeSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounterSensor == null) {
            Toast.makeText(this, "No step counter sensor found on this device", Toast.LENGTH_LONG).show()
            return
        }

        // Request permission for Android 10 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestActivityPermission()
        } else {
            registerStepSensor()
        }
    }

    private fun requestActivityPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                1
            )
        } else {
            registerStepSensor()
        }
    }

    private fun registerStepSensor() {
        stepCounterSensor?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onResume() {
        super.onResume()
        registerStepSensor()
    }

    override fun onPause() {
        super.onPause()
        // Don't unregister the sensor as it will stop counting steps
        // Only unregister if you want to stop counting when app is in background
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerStepSensor()
                } else {
                    stepCountTextView.text = "Permission denied to access step count"
                }
            }
            2 -> {
                if (stepNotification.handlePermissionResult(grantResults)) {
                    quoteNotification.showNotification("You're important too!", "InspireBot")
                } else {
                    Toast.makeText(
                        this,
                        "Permission denied for notifications",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            if (isFirstReading) {
                initialStepCount = event.values[0].toInt()
                isFirstReading = false
                return
            }

            val totalSteps = event.values[0].toInt()
            stepCount = totalSteps - initialStepCount
            runOnUiThread { updateStepCountUI(stepCount) }
            onStepDetectedReset()
        }
    }

    private fun onStepDetectedReset() {
        stepCount = stepCountResetter.checkAndResetStepCount(stepCount)
        updateStepCountUI(stepCount)
        stepCountWriter.saveDailyStepCount(stepCount)
    }

    private fun updateStepCountUI(stepCount: Int) {
        stepCountTextView.text = "Steps: $stepCount"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }
}

