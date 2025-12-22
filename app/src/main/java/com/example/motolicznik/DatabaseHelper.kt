package com.example.motolicznik

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.example.motolicznik.HoursEntry

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MotoLicznik.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_HOURS = "hours"
        private const val COLUMN_ID = "id"
        private const val COLUMN_HOURS = "hours"
        private const val COLUMN_DATE = "date"
        private const val BACKUP_FILE_NAME = "backup.json"
    }

    private val appContext = context.applicationContext

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_HOURS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_HOURS REAL NOT NULL,
                $COLUMN_DATE TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HOURS")
        onCreate(db)
    }

    fun addHours(hours: Double): Long {
        val db = this.writableDatabase
        val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(COLUMN_HOURS, hours)
            put(COLUMN_DATE, date)
        }
        val result = db.insert(TABLE_HOURS, null, values)
        exportToBackupFile() // Always update backup
        return result
    }

    fun getAllHours(): List<HoursEntry> {
        val hoursList = mutableListOf<HoursEntry>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_HOURS,
            arrayOf(COLUMN_ID, COLUMN_HOURS, COLUMN_DATE),
            null,
            null,
            null,
            null,
            "$COLUMN_ID DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val hours = getDouble(getColumnIndexOrThrow(COLUMN_HOURS))
                val date = getString(getColumnIndexOrThrow(COLUMN_DATE))
                hoursList.add(HoursEntry(id, hours, date))
            }
        }
        cursor.close()
        return hoursList
    }

    fun updateHours(id: Long, hours: Double) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_HOURS, hours)
        }
        db.update(TABLE_HOURS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        exportToBackupFile() // Always update backup
    }

    fun deleteHours(id: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_HOURS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        exportToBackupFile() // Always update backup
    }

    fun exportToBackupFile() {
        val hoursList = getAllHours()
        val jsonArray = JSONArray()
        for (entry in hoursList) {
            val obj = JSONObject()
            obj.put("id", entry.id)
            obj.put("hours", entry.hours)
            obj.put("date", entry.date)
            jsonArray.put(obj)
        }
        val file = File(appContext.getExternalFilesDir(null), BACKUP_FILE_NAME)
        try {
            FileWriter(file).use { it.write(jsonArray.toString()) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun importFromBackupFile(): List<HoursEntry> {
        val file = File(appContext.getExternalFilesDir(null), BACKUP_FILE_NAME)
        val importedList = mutableListOf<HoursEntry>()
        if (!file.exists()) return importedList
        try {
            val json = file.readText()
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                importedList.add(
                    HoursEntry(
                        id = obj.optLong("id", 0L),
                        hours = obj.getDouble("hours"),
                        date = obj.getString("date")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return importedList
    }

    fun isBackupFileExists(): Boolean {
        val file = File(appContext.getExternalFilesDir(null), BACKUP_FILE_NAME)
        return file.exists()
    }
}