package com.example.motolicznik

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.motolicznik.databinding.ItemHourEntryBinding

class HoursAdapter(
    private val formatter: (Double) -> String,
    private val onEditClick: (HoursEntry) -> Unit,
    private val onDeleteClick: (HoursEntry) -> Unit
) : ListAdapter<HoursEntry, HoursAdapter.HoursViewHolder>(HoursDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val binding = ItemHourEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HoursViewHolder(binding, formatter, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class HoursViewHolder(
        private val binding: ItemHourEntryBinding,
        private val formatter: (Double) -> String,
        private val onEditClick: (HoursEntry) -> Unit,
        private val onDeleteClick: (HoursEntry) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hoursEntry: HoursEntry) {
            binding.apply {
                // Poprawiona, czytelna wersja
                textViewHours.text = formatter(hoursEntry.hours)

                buttonEdit.setOnClickListener {
                    onEditClick(hoursEntry)
                }
                buttonDelete.setOnClickListener {
                    onDeleteClick(hoursEntry)
                }
            }
        }
    }

    class HoursDiffCallback : DiffUtil.ItemCallback<HoursEntry>() {
        override fun areItemsTheSame(oldItem: HoursEntry, newItem: HoursEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HoursEntry, newItem: HoursEntry): Boolean {
            return oldItem == newItem
        }
    }
}