package com.example.motolicznik

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.motolicznik.databinding.ActivityMainBinding
import com.example.motolicznik.databinding.DialogEditHoursBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isRunning = false
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateTimer()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startStopButton.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        binding.resetButton.setOnClickListener {
            resetTimer()
        }

        binding.saveButton.setOnClickListener {
            showSaveDialog()
        }

        binding.savedHoursButton.setOnClickListener {
            val intent = Intent(this, SavedHoursActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime
            isRunning = true
            binding.startStopButton.text = "Stop"
            handler.post(updateTimeRunnable)
        }
    }

    private fun stopTimer() {
        if (isRunning) {
            isRunning = false
            binding.startStopButton.text = "Start"
            handler.removeCallbacks(updateTimeRunnable)
        }
    }

    private fun resetTimer() {
        stopTimer()
        elapsedTime = 0
        updateTimer()
    }

    private fun updateTimer() {
        if (isRunning) {
            elapsedTime = System.currentTimeMillis() - startTime
        }
        val hours = elapsedTime / (1000 * 60 * 60)
        val minutes = (elapsedTime % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (elapsedTime % (1000 * 60)) / 1000
        binding.timerText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun showSaveDialog() {
        val dialogBinding = DialogEditHoursBinding.inflate(layoutInflater)
        val hours = elapsedTime / (1000 * 60 * 60)
        val minutes = (elapsedTime % (1000 * 60 * 60)) / (1000 * 60)
        dialogBinding.hoursInput.setText(String.format("%d.%02d", hours, minutes))

        AlertDialog.Builder(this)
            .setTitle("Zapisz godziny")
            .setView(dialogBinding.root)
            .setPositiveButton("Zapisz") { _, _ ->
                val hoursText = dialogBinding.hoursInput.text.toString()
                try {
                    val hours = hoursText.toDouble()
                    val dbHelper = DatabaseHelper(this)
                    dbHelper.addHours(hours)
                    Toast.makeText(this, "Zapisano godziny", Toast.LENGTH_SHORT).show()
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Nieprawidłowy format", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
    }
}