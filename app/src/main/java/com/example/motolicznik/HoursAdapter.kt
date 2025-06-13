package com.example.motolicznik

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.motolicznik.databinding.ItemHoursBinding

class HoursAdapter(
    private val onEditClick: (HoursEntry) -> Unit,
    private val onDeleteClick: (HoursEntry) -> Unit
) : ListAdapter<HoursEntry, HoursAdapter.HoursViewHolder>(HoursDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val binding = ItemHoursBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HoursViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HoursViewHolder(
        private val binding: ItemHoursBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hours: HoursEntry) {
            binding.numberText.text = (adapterPosition + 1).toString()
            binding.hoursText.text = String.format("%.2f", hours.hours)
            binding.dateText.text = hours.date

            binding.editButton.setOnClickListener {
                onEditClick(hours)
            }

            binding.deleteButton.setOnClickListener {
                onDeleteClick(hours)
            }
        }
    }

    private class HoursDiffCallback : DiffUtil.ItemCallback<HoursEntry>() {
        override fun areItemsTheSame(oldItem: HoursEntry, newItem: HoursEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HoursEntry, newItem: HoursEntry): Boolean {
            return oldItem == newItem
        }
    }
} 