package com.example.motolicznik

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

        dbHelper = DatabaseHelper(this)
        setupRecyclerView()
        loadHours()
    }

    private fun setupRecyclerView() {
        adapter = HoursAdapter(
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

    private fun showEditDialog(hours: Hours) {
        val dialogBinding = DialogEditHoursBinding.inflate(layoutInflater)
        dialogBinding.hoursInput.setText(hours.hours.toString())

        AlertDialog.Builder(this)
            .setTitle("Edytuj godziny")
            .setView(dialogBinding.root)
            .setPositiveButton("Zapisz") { _, _ ->
                val hoursText = dialogBinding.hoursInput.text.toString()
                try {
                    val newHours = hoursText.toDouble()
                    dbHelper.updateHours(hours.id, newHours)
                    loadHours()
                } catch (e: NumberFormatException) {
                    // Handle invalid input
                }
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    private fun showDeleteConfirmation(hours: Hours) {
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
} 