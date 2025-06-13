package com.example.motolicznik

import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SavedHoursActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: HoursAdapter
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_hours)

        db = DatabaseHelper(this)
        listView = findViewById(R.id.hoursListView)
        
        loadHours()
    }

    private fun loadHours() {
        val hoursList = db.getAllHours()
        adapter = HoursAdapter(this, hoursList) { id, hours ->
            showEditDialog(id, hours)
        }
        listView.adapter = adapter
    }

    private fun showEditDialog(id: Long, currentHours: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_hours, null)
        val editText = dialogView.findViewById<EditText>(R.id.editHoursEditText)
        editText.setText(currentHours)

        AlertDialog.Builder(this)
            .setTitle("Edytuj godziny")
            .setView(dialogView)
            .setPositiveButton("Zapisz") { _, _ ->
                val newHours = editText.text.toString()
                if (isValidTimeFormat(newHours)) {
                    db.updateHours(id, newHours)
                    loadHours()
                } else {
                    Toast.makeText(this, "Nieprawidłowy format czasu", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    private fun isValidTimeFormat(time: String): Boolean {
        return time.matches(Regex("^([0-9]{2}):([0-9]{2})$"))
    }

    override fun onResume() {
        super.onResume()
        loadHours()
    }
} 