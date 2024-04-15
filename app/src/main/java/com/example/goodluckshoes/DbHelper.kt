package com.example.goodluckshoes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream


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

//    fun getExpense(): Cursor? {
//        val db = this.readableDatabase
//        val selection = "${FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
//        val selectionArgs = arrayOf("MyTitle")
//        val deletedRows = db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs)
//    }

    fun getExpense(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $DATE_COl", null)
    }

    fun getExpenseByDate(date: String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $CREATED_DATE_COL  = ?", arrayOf(date))
    }

    fun getDayList(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT DISTINCT $CREATED_DATE_COL FROM $TABLE_NAME ORDER BY $CREATED_DATE_COL",
            null
        )
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

    //close database
    fun closeDB() {
        val db = this.readableDatabase
        if (db != null && db.isOpen)
            db.close()
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
            Toast.makeText(context,  "Unable to import database. Retry", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}