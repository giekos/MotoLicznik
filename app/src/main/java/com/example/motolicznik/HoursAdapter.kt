package com.example.motolicznik

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class HoursAdapter(
    context: Context,
    private val hoursList: List<HoursEntry>,
    private val onEditClick: (Long, String) -> Unit
) : ArrayAdapter<HoursEntry>(context, 0, hoursList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_hours, parent, false)

        val entry = hoursList[position]
        val db = DatabaseHelper(context)

        view.findViewById<TextView>(R.id.numberTextView).text = (position + 1).toString()
        view.findViewById<TextView>(R.id.hoursTextView).text = entry.hours
        view.findViewById<TextView>(R.id.dateTextView).text = entry.date

        view.findViewById<Button>(R.id.editButton).setOnClickListener {
            onEditClick(entry.id, entry.hours)
        }

        view.findViewById<Button>(R.id.deleteButton).setOnClickListener {
            db.deleteHours(entry.id)
            notifyDataSetChanged()
            Toast.makeText(context, "Usunięto wpis", Toast.LENGTH_SHORT).show()
        }

        return view
    }
} 