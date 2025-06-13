package com.example.motolicznik

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var timerTextView: TextView
    private lateinit var startStopButton: Button
    private lateinit var saveButton: Button
    private lateinit var manualInputEditText: EditText
    private lateinit var manualSaveButton: Button
    private lateinit var listButton: Button

    private var isRunning = false
    private var seconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                seconds++
                updateTimerDisplay()
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        timerTextView = findViewById(R.id.timerTextView)
        startStopButton = findViewById(R.id.startStopButton)
        saveButton = findViewById(R.id.saveButton)
        manualInputEditText = findViewById(R.id.manualInputEditText)
        manualSaveButton = findViewById(R.id.manualSaveButton)
        listButton = findViewById(R.id.listButton)
    }

    private fun setupClickListeners() {
        startStopButton.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        saveButton.setOnClickListener {
            saveTimerValue()
        }

        manualSaveButton.setOnClickListener {
            saveManualValue()
        }

        listButton.setOnClickListener {
            startActivity(android.content.Intent(this, SavedHoursActivity::class.java))
        }
    }

    private fun startTimer() {
        isRunning = true
        startStopButton.text = "Stop"
        handler.post(runnable)
    }

    private fun stopTimer() {
        isRunning = false
        startStopButton.text = "Start"
        handler.removeCallbacks(runnable)
    }

    private fun updateTimerDisplay() {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        timerTextView.text = String.format("%02d:%02d", hours, minutes)
    }

    private fun saveTimerValue() {
        if (seconds == 0) {
            Toast.makeText(this, "Najpierw zmierz czas", Toast.LENGTH_SHORT).show()
            return
        }

        val hours = formatTime(seconds)
        saveHoursToDatabase(hours)
        seconds = 0
        updateTimerDisplay()
    }

    private fun saveManualValue() {
        val input = manualInputEditText.text.toString()
        if (!isValidTimeFormat(input)) {
            Toast.makeText(this, "Wprowadź czas w formacie hh:mm", Toast.LENGTH_SHORT).show()
            return
        }

        saveHoursToDatabase(input)
        manualInputEditText.text.clear()
    }

    private fun saveHoursToDatabase(hours: String) {
        val db = DatabaseHelper(this)
        val date = SimpleDateFormat("dd:MM:yy", Locale.getDefault()).format(Date())
        db.addHours(hours, date)
        Toast.makeText(this, "Zapisano godziny", Toast.LENGTH_SHORT).show()
    }

    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return String.format("%02d:%02d", hours, minutes)
    }

    private fun isValidTimeFormat(time: String): Boolean {
        return time.matches(Regex("^([0-9]{2}):([0-9]{2})$"))
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}