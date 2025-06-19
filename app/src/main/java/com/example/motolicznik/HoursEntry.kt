package com.example.motolicznik

data class HoursEntry(
    val id: Long,       // Lub Long, jeśli ID z bazy danych jest typu Long
    val hours: Double,   // Lub inny typ, jeśli przechowujesz inaczej
    val date: String   // Przykładowe pole na datę, dostosuj wg potrzeb
)