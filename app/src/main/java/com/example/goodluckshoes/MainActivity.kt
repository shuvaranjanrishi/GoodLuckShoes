package com.example.goodluckshoes

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.goodluckshoes.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    var binding: ActivityMainBinding? = null

    private val calendar = Calendar.getInstance()
    private lateinit var adapter: MainListAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var db: DbHelper

    private var localBackup: LocalBackup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)

        setTodayDate()

        localBackup = LocalBackup(this)
        db = DbHelper(this, null)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.data.observe(this) {

            var total1 = "0"
            var total2 = "0"
            var total3 = "0"
            var total4 = "0"

            Log.d(TAG, "Data Size: ${it.size}")
            adapter = MainListAdapter(it)
            binding!!.recyclerView.adapter = adapter

            for (item in it) {
                total1 = (total1.toDouble() + item.text1.toDouble()).toString()
                total2 = (total2.toDouble() + item.text2.toDouble()).toString()
                total3 = (total3.toDouble() + item.text3.toDouble()).toString()
                total4 = (total4.toDouble() + item.text4.toDouble()).toString()
            }

            binding!!.totalTv.text = "মোট\n(" + it.size.toString() + ")"
            binding!!.total1.text = total1
            binding!!.total2.text = total2
            binding!!.total3.text = total3
            binding!!.total4.text = total4
        }

        viewModel.getData(db)

        binding!!.backupBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == (PackageManager.PERMISSION_GRANTED)
            ) {
                db.performFullBackup(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
                )
            }

//            val outFileName = Environment.getExternalStorageDirectory()
//                .toString() + File.separator + resources.getString(R.string.app_name) + File.separator
//            localBackup!!.performBackup(db, outFileName)
        }
        binding!!.restoreBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == (PackageManager.PERMISSION_GRANTED)
            ) {
                db.performFullRestore(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
                )
            }
//            val outFileName = Environment.getExternalStorageDirectory()
//                .toString() + File.separator + resources.getString(R.string.app_name) + File.separator
//            localBackup!!.performBackup(db, outFileName)
        }
        binding!!.viewAllBtn.setOnClickListener {
            startActivity(Intent(this, DayListActivity::class.java))
        }
        binding!!.dateEt.setOnClickListener {
            showDatePicker()
        }

        binding!!.saveBtn.setOnClickListener {

            val date: String = binding!!.dateEt.text.toString()
            val str1: String =
                if (binding!!.et2.text.toString() == "") "0" else binding!!.et2.text.toString()
            val str2: String =
                if (binding!!.et3.text.toString() == "") "0" else binding!!.et3.text.toString()
            val str3: String =
                if (binding!!.et4.text.toString() == "") "0" else binding!!.et4.text.toString()
            val str4: String =
                if (binding!!.et5.text.toString() == "") "0" else binding!!.et5.text.toString()

            if (date.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please enter Income Date", Toast.LENGTH_SHORT)
                    .show()
            } else if (str1 == "0" && str2 == "0" && str3 == "0" && str4 == "0") {
                Toast.makeText(this@MainActivity, "Please enter Amount", Toast.LENGTH_SHORT)
                    .show()
            } else {

                val selectedDate = Calendar.getInstance()
                selectedDate.timeInMillis = System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
                val createdAt = dateFormat.format(selectedDate.time)

                db.addExpense(
                    "" + date,
                    "" + str1,
                    "" + str2,
                    "" + str3,
                    "" + str4,
                    "" + createdAt,
                )
                Toast.makeText(this, "Data added to database", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun setTodayDate() {
        val selectedDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        binding!!.todayDateTv.text = "" + dateFormat.format(selectedDate.time)
    }

    @SuppressLint("Range")
    private fun populateRecyclerView() {

        var total1 = "0"
        var total2 = "0"
        var total3 = "0"
        var total4 = "0"

        val db = DbHelper(this, null)
        val cursor = db.getExpense()!!

        val data = ArrayList<ItemModel>()
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(cursor.getColumnIndex(DbHelper.DATE_COl))
                val amount1 = cursor.getString(cursor.getColumnIndex(DbHelper.AMOUNT1_COl))
                val amount2 = cursor.getString(cursor.getColumnIndex(DbHelper.AMOUNT2_COl))
                val amount3 = cursor.getString(cursor.getColumnIndex(DbHelper.AMOUNT3_COl))
                val amount4 = cursor.getString(cursor.getColumnIndex(DbHelper.AMOUNT4_COl))
                val createdAt = cursor.getString(cursor.getColumnIndex(DbHelper.CREATED_DATE_COL))

                data.add(ItemModel(date, amount1, amount2, amount3, amount4))
                Log.d("TAG", "List: $data")
            } while (cursor.moveToNext());
        }

//        adapter = MainListAdapter(data)
//        binding!!.recyclerView.adapter = adapter

        for (item in data) {
            total1 = (total1.toDouble() + item.text1.toDouble()).toString()
            total2 = (total2.toDouble() + item.text2.toDouble()).toString()
            total3 = (total3.toDouble() + item.text3.toDouble()).toString()
            total4 = (total4.toDouble() + item.text4.toDouble()).toString()
        }

        binding!!.totalTv.text = "মোট\n(" + data.size.toString() + ")"
        binding!!.total1.text = total1
        binding!!.total2.text = total2
        binding!!.total3.text = total3
        binding!!.total4.text = total4

        cursor.close()
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding!!.dateEt.setText("$formattedDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}