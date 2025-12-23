package com.example.motolicznik

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motolicznik.databinding.ActivitySavedHoursBinding
import com.example.motolicznik.databinding.DialogEditHoursBinding

class SavedHoursActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedHoursBinding
    private lateinit var adapter: HoursAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedHoursBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Zapisane godziny"

        dbHelper = DatabaseHelper(this)
        setupRecyclerView()
        loadHours()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupRecyclerView() {
        adapter = HoursAdapter(
            // Przekazujemy funkcję formatującą do adaptera
            formatter = ::formatHoursToHHMMSS,
            onEditClick = { hours ->
                showEditDialog(hours)
            },
            onDeleteClick = { hours ->
                showDeleteConfirmation(hours)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadHours() {
        val hours = dbHelper.getAllHours()
        adapter.submitList(hours)
    }

    // ZMODYFIKOWANA FUNKCJA
    private fun showEditDialog(hoursEntry: HoursEntry) {
        // Używamy tego samego layoutu co w MainActivity
        val dialogBinding = DialogEditHoursBinding.inflate(layoutInflater)

        // Rozbijamy liczbę ułamkową na części HH, MM, SS
        val totalSeconds = (hoursEntry.hours * 3600).toLong()
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60

        // Ustawiamy tekst w polach
        dialogBinding.hoursInput.setText(h.toString())
        dialogBinding.minutesInput.setText(m.toString())
        dialogBinding.secondsInput.setText(s.toString())

        AlertDialog.Builder(this)
            .setTitle("Edytuj godziny")
            .setView(dialogBinding.root)
            .setPositiveButton("Zapisz") { _, _ ->
                try {
                    // Odczytujemy wartości z pól
                    val newH = dialogBinding.hoursInput.text.toString().toLongOrNull() ?: 0L
                    val newM = dialogBinding.minutesInput.text.toString().toLongOrNull() ?: 0L
                    val newS = dialogBinding.secondsInput.text.toString().toLongOrNull() ?: 0L

                    // Składamy z powrotem w jedną liczbę Double
                    val newTotalHours = newH.toDouble() + (newM.toDouble() / 60.0) + (newS.toDouble() / 3600.0)

                    dbHelper.updateHours(hoursEntry.id, newTotalHours)
                    loadHours()
                } catch (e: NumberFormatException) {
                    // Handle invalid input
                }
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    private fun showDeleteConfirmation(hours: HoursEntry) {
        AlertDialog.Builder(this)
            .setTitle("Usuń wpis")
            .setMessage("Czy na pewno chcesz usunąć ten wpis?")
            .setPositiveButton("Tak") { _, _ ->
                dbHelper.deleteHours(hours.id)
                loadHours()
            }
            .setNegativeButton("Nie", null)
            .show()
    }

    // DODANA FUNKCJA POMOCNICZA
    @SuppressLint("DefaultLocale")
    private fun formatHoursToHHMMSS(totalHours: Double): String {
        if (totalHours < 0) {
            return "00:00:00"
        }
        val totalSeconds = (totalHours * 3600).toLong()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}