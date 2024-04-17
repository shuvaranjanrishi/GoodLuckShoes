package com.example.goodluckshoes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.channels.FileChannel


class DbHelper(private val context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                DATE_COl + " TEXT," +
                AMOUNT1_COl + " TEXT," +
                AMOUNT2_COl + " TEXT," +
                AMOUNT3_COl + " TEXT," +
                AMOUNT4_COl + " TEXT," +
                CREATED_DATE_COL + " TEXT" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addExpense(
        date: String,
        amount1: String,
        amount2: String,
        amount3: String,
        amount4: String,
        createdAt: String
    ) {
        val values = ContentValues()
        values.put(DATE_COl, date)
        values.put(AMOUNT1_COl, amount1)
        values.put(AMOUNT2_COl, amount2)
        values.put(AMOUNT3_COl, amount3)
        values.put(AMOUNT4_COl, amount4)
        values.put(CREATED_DATE_COL, createdAt)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateExpense(
        id: String,
        amount1: String,
        amount2: String,
        amount3: String,
        amount4: String,
        createdAt: String
    ) {
        val values = ContentValues()
        values.put(AMOUNT1_COl, amount1)
        values.put(AMOUNT2_COl, amount2)
        values.put(AMOUNT3_COl, amount3)
        values.put(AMOUNT4_COl, amount4)
        values.put(CREATED_DATE_COL, createdAt)

        val db = this.writableDatabase
        db.update(TABLE_NAME, values, "$ID_COL=?", arrayOf(id))
        db.close()
    }

    fun getExpense(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $DATE_COl DESC", null)
    }

    fun getExpenseByDate(date: String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $CREATED_DATE_COL  = ? ORDER BY $DATE_COl DESC",
            arrayOf(date)
        )
    }

    fun getDayList(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT DISTINCT $CREATED_DATE_COL FROM $TABLE_NAME ORDER BY $CREATED_DATE_COL",
            null
        )
    }

    fun deleteExpense(id: String): Int {
        val db = this.readableDatabase
        return db.delete(TABLE_NAME, "$ID_COL=$id", null)
    }

    companion object {
        private val DATABASE_NAME = "GOODLUCK_SHOES"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "daily_expense"
        val ID_COL = "id"
        val DATE_COl = "income_date"
        val AMOUNT1_COl = "amount1"
        val AMOUNT2_COl = "amount2"
        val AMOUNT3_COl = "amount3"
        val AMOUNT4_COl = "amount4"
        val CREATED_DATE_COL = "create_at"
    }

    fun backup(outFileName: String?) {

        //database path
        val inFileName: String = context.getDatabasePath(DATABASE_NAME).toString()
        try {
            val dbFile = File(inFileName)
            val fis = FileInputStream(dbFile)

            // Open the empty db as the output stream
            val output: OutputStream = FileOutputStream(outFileName)

            // Transfer bytes from the input file to the output file
            val buffer = ByteArray(1024)
            var length: Int
            while (fis.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }

            // Close the streams
            output.flush()
            output.close()
            fis.close()
            Toast.makeText(context, "Backup Completed", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to backup database. Retry", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    fun importDB(inFileName: String?) {
        val outFileName: String = context.getDatabasePath(DATABASE_NAME).toString()
        try {
            val dbFile = File(inFileName)
            val fis = FileInputStream(dbFile)

            // Open the empty db as the output stream
            val output: OutputStream = FileOutputStream(outFileName)

            // Transfer bytes from the input file to the output file
            val buffer = ByteArray(1024)
            var length: Int
            while (fis.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }

            // Close the streams
            output.flush()
            output.close()
            fis.close()
            Toast.makeText(context, "Import Completed", Toast.LENGTH_LONG).show()
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, "Unable to import database. Retry", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    fun performFullBackup(context: Context) {
        try {
            val originalDbFile: File = context.getDatabasePath(DATABASE_NAME)
            val backupFilePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/SQLiteBackup")
                    .toString()
            val backupDir = File(backupFilePath)
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            val backupFileName = "ListData.db"
            val backupDbFile = File(backupDir, backupFileName)
            if (backupDbFile.exists()) {
                backupDbFile.delete()
            }

            if (originalDbFile.exists()) {
                val src: FileChannel = FileInputStream(originalDbFile).channel
                val dst: FileChannel = FileInputStream(backupDbFile).channel

                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                Toast.makeText(context, "Backup Successful...", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Original Database File not Found...", Toast.LENGTH_LONG)
                    .show()
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error: " + e.message, Toast.LENGTH_LONG).show()
        }

    }

    fun performFullRestore(context: Context) {
        try {
            val backupFilePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/SQLiteBackup")
                    .toString()
            val backupDir = File(backupFilePath)
            val backupFileName = "ListData.db"
            val backupDbFile = File(backupDir, backupFileName)

            if (backupDbFile.exists()) {
                val originalDbFile: File = context.getDatabasePath(DATABASE_NAME)

                val src: FileChannel = FileInputStream(backupDbFile).channel
                val dst: FileChannel = FileInputStream(originalDbFile).channel

                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                Toast.makeText(context, "Restore Successful...", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Backup File not Found...", Toast.LENGTH_LONG).show()
            }


        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error: " + e.message, Toast.LENGTH_LONG).show()
        }

    }
}