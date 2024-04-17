package com.example.goodluckshoes

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.goodluckshoes.databinding.ActivityMainBinding
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    var binding: ActivityMainBinding? = null

    private val calendar = Calendar.getInstance()
    private lateinit var adapter: MainListAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var db: DbHelper
    private val STORAGE_REQUEST_CODE_EXPORT = 100
    private val STORAGE_REQUEST_CODE_IMPORT = 200
    private var dataList = mutableListOf<ItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)

        setTodayDate()

        db = DbHelper(this, null)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.data.observe(this) {

            var total1 = "0"
            var total2 = "0"
            var total3 = "0"
            var total4 = "0"

            Log.d(TAG, "Data Size: ${it.size}")
            dataList = it
            adapter = MainListAdapter(this, it)
            binding!!.recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

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
            if (checkStoragePermission()) {
                exportCSV()
            } else {
                requestStoragePermissionExport()
            }
        }

        binding!!.restoreBtn.setOnClickListener {
            if (checkStoragePermission()) {
                importCSV()
            } else {
//                requestStoragePermissionImport()
            }
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
                Toast.makeText(
                    this@MainActivity,
                    "" + resources.getString(R.string.enter_expense_date),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (str1 == "0" && str2 == "0" && str3 == "0" && str4 == "0") {
                Toast.makeText(
                    this@MainActivity,
                    "" + resources.getString(R.string.enter_amount),
                    Toast.LENGTH_SHORT
                )
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
                Toast.makeText(
                    this,
                    "" + resources.getString(R.string.data_saved),
                    Toast.LENGTH_LONG
                ).show()
                clearFields()
            }
        }

    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
    }

//    private fun requestStoragePermissionImport() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(WRITE_EXTERNAL_STORAGE),
//            STORAGE_REQUEST_CODE_IMPORT
//        )
//    }

    private fun requestStoragePermissionExport() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(WRITE_EXTERNAL_STORAGE),
            STORAGE_REQUEST_CODE_EXPORT
        )
    }

    private fun clearFields() {
        binding!!.et2.setText("")
        binding!!.et3.setText("")
        binding!!.et4.setText("")
        binding!!.et5.setText("")
    }

    private fun setTodayDate() {
        val selectedDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        binding!!.todayDateTv.text = "" + dateFormat.format(selectedDate.time)
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                calendar.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
                val formattedDate = dateFormat.format(calendar.time)
                binding!!.dateEt.setText("$formattedDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun exportCSV() {
        val folder = File(Environment.getExternalStorageDirectory(), "SQLiteBackup")
        var isFolderCreated = false
        if (!folder.exists()) {
            isFolderCreated = folder.mkdir()
        }

        val csvFileName = "SQLite_Backup.csv"
        val csvPathAndName = "$folder/$csvFileName"

        var recordList: MutableList<ItemModel> = mutableListOf()
        recordList.clear()

        recordList = dataList

        try {
            val fileWriter = FileWriter(csvPathAndName)
            for (i in 1..dataList.size) {
                Log.d(TAG, "asdfasdfasdf: " + recordList[i].id + "     " + recordList[i].text1)
                fileWriter.append("" + recordList[i].id)
                fileWriter.append(",")
                fileWriter.append("" + recordList[i].date)
                fileWriter.append(",")
                fileWriter.append("" + recordList[i].text1)
                fileWriter.append(",")
                fileWriter.append("" + recordList[i].text2)
                fileWriter.append(",")
                fileWriter.append("" + recordList[i].text3)
                fileWriter.append(",")
                fileWriter.append("" + recordList[i].text4)
                fileWriter.append(",")
                fileWriter.append("" + recordList[i].createDate)
                fileWriter.append(",")

                fileWriter.flush()
                fileWriter.close()

                Toast.makeText(
                    this,
                    "" + resources.getString(R.string.backup_done) + " " + csvPathAndName,
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "" + e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun importCSV() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

//        if (requestCode == STORAGE_REQUEST_CODE_EXPORT) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                exportCSV()
//            } else {
//                Toast.makeText(
//                    this,
//                    "" + resources.getString(R.string.storage_permission),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }

        when (requestCode) {
            STORAGE_REQUEST_CODE_EXPORT -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "" + resources.getString(R.string.storage_permission), Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
//        if (requestCode == STORAGE_REQUEST_CODE_IMPORT) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                importCSV()
//            } else {
//                Toast.makeText(
//                    this,
//                    "" + resources.getString(R.string.storage_permission),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }

    }
}