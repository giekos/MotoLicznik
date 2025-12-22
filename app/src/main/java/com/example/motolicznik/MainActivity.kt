package com.example.motolicznik

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.motolicznik.databinding.ActivityMainBinding
import com.example.motolicznik.databinding.DialogEditHoursBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isRunning = false
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime
                updateTimerDisplay()
                handler.postDelayed(this, 1000)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Krok 1: Odtwórz zapisany stan, jeśli istnieje
        if (savedInstanceState != null) {
            elapsedTime = savedInstanceState.getLong("elapsedTime", 0L)
            startTime = savedInstanceState.getLong("startTime", 0L)
            isRunning = savedInstanceState.getBoolean("isRunning", false)
        }

        // Check for first run and backup restore
        checkAndPromptRestoreBackup()

        // Ustawienie listenerów
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

        // Krok 2: Zsynchronizuj UI z odtworzonym (lub domyślnym) stanem
        updateTimerDisplay() // Wyświetl poprawny czas
        if (isRunning) {
            binding.startStopButton.text = "Stop" // Ustaw tekst przycisku
            handler.post(updateTimeRunnable)     // Wznów pętlę, jeśli stoper działał
        } else {
            binding.startStopButton.text = "Start" // Ustaw tekst przycisku, jeśli stoper był zatrzymany
        }
    }

    @SuppressLint("UseKtx")
    private fun checkAndPromptRestoreBackup() {
        val prefs = getSharedPreferences("motolicznik_prefs", MODE_PRIVATE)
        val alreadyRestored = prefs.getBoolean("already_restored", false)
        val dbHelper = DatabaseHelper(this)
        val dbEmpty = dbHelper.getAllHours().isEmpty()
        val backupExists = dbHelper.isBackupFileExists()
        if (!alreadyRestored && dbEmpty && backupExists) {
            AlertDialog.Builder(this)
                .setTitle("Przywracanie danych")
                .setMessage("Czy chcesz przywrócić stare dane?")
                .setPositiveButton("Tak") { _, _ ->
                    val imported = dbHelper.importFromBackupFile()
                    for (entry in imported) {
                        dbHelper.addHours(entry.hours)
                    }
                    prefs.edit().putBoolean("already_restored", true).apply()
                    Toast.makeText(this, "Przywrócono dane", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Nie") { _, _ ->
                    prefs.edit().putBoolean("already_restored", true).apply()
                }
                .show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime
            isRunning = true
            binding.startStopButton.text = "Stop"
            handler.post(updateTimeRunnable)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun stopTimer() {
        if (isRunning) {
            isRunning = false
            handler.removeCallbacks(updateTimeRunnable)
            elapsedTime = System.currentTimeMillis() - startTime
            binding.startStopButton.text = "Start"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun resetTimer() {
        isRunning = false
        handler.removeCallbacks(updateTimeRunnable)
        elapsedTime = 0
        startTime = 0
        updateTimerDisplay()
        binding.startStopButton.text = "Start"
    }

    @SuppressLint("DefaultLocale")
    private fun updateTimerDisplay() {
        val hours = elapsedTime / 3600000
        val minutes = (elapsedTime % 3600000) / 60000
        val seconds = (elapsedTime % 60000) / 1000
        binding.timerText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    @SuppressLint("DefaultLocale")
    private fun showSaveDialog() {
        val dialogBinding = DialogEditHoursBinding.inflate(layoutInflater)
        val hours = elapsedTime / 3600000
        val minutes = (elapsedTime % 3600000) / 60000
        val totalHours = hours + (minutes / 60.0)

        // Użycie Locale.US, aby separatorem zawsze była kropka
        dialogBinding.hoursInput.setText(String.format(Locale.US, "%.2f", totalHours))

        AlertDialog.Builder(this)
            .setTitle("Zapisz godziny")
            .setView(dialogBinding.root)
            .setPositiveButton("Zapisz") { _, _ ->
                val hoursText = dialogBinding.hoursInput.text.toString()
                try {
                    val hoursValue = hoursText.toDouble()
                    val dbHelper = DatabaseHelper(this)
                    dbHelper.addHours(hoursValue)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("elapsedTime", elapsedTime)
        outState.putLong("startTime", startTime)
        outState.putBoolean("isRunning", isRunning)
    }
}