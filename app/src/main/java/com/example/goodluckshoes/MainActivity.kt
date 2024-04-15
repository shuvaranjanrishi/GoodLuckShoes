package com.example.goodluckshoes

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.goodluckshoes.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null

    private val calendar = Calendar.getInstance()
    var amount1 = "0"
    var amount2 = "0"
    var amount3 = "0"
    var amount4 = "0"

    var adapter: MainListAdapter? = null

    private var localBackup: LocalBackup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)

        setTodayDate()

        localBackup = LocalBackup(this);

        val  db  = DbHelper(this,null);
//        setupUI(db)
        db.closeDB()

        binding!!.backupBtn.setOnClickListener {
            val outFileName = Environment.getExternalStorageDirectory()
                .toString() + File.separator + resources.getString(R.string.app_name) + File.separator
            localBackup!!.performBackup(db, outFileName)
        }
        binding!!.viewAllBtn.setOnClickListener {
            startActivity(Intent(this, DayListActivity::class.java))
        }
        binding!!.dateEt.setOnClickListener {
            showDatePicker()
        }

        binding!!.saveBtn.setOnClickListener {

            val date: String = binding!!.dateEt.text.toString()
            val str1: String = binding!!.et2.text.toString() + "0"
            val str2: String = binding!!.et3.text.toString() + "0"
            val str3: String = binding!!.et4.text.toString() + "0"
            val str4: String = binding!!.et5.text.toString() + "0"

            if (date.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please enter Income Date", Toast.LENGTH_SHORT)
                    .show()
            } else if (str1 == "0" && str2 == "0" && str3 == "0" && str4 == "0") {
                Toast.makeText(this@MainActivity, "Please enter Amount", Toast.LENGTH_SHORT)
                    .show()
            } else {

                val db = DbHelper(this, null)

//                val createdAt = "" + System.currentTimeMillis()

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
                adapter!!.notifyDataSetChanged()
                Toast.makeText(this, "$date added to database", Toast.LENGTH_LONG).show()
            }
        }

    }

//    fun setupUI(db: DbHelper) {
//        val fab = findViewById<FloatingActionButton>(R.id.fab)
//        fab.setOnClickListener { v: View? -> showMultichoice() }
//
//        //button that shows student, exam and clears the view
//        val showStud = findViewById<Button>(R.id.button)
//        val showExam = findViewById<Button>(R.id.button2)
//        val clear = findViewById<Button>(R.id.button3)
//        showStud.setOnClickListener { v: View? ->
//            val students: ArrayList<Student> = db.getAllStudent()
//            val table = findViewById<TableLayout>(R.id.table)
//            //table customization
//            val layoutParamsT =
//                TableLayout.LayoutParams(
//                    TableLayout.LayoutParams.MATCH_PARENT,
//                    TableLayout.LayoutParams.WRAP_CONTENT
//                )
//            layoutParamsT.setMargins(30, 20, 40, 0)
//            table.removeAllViews()
//            val row = TableRow(applicationContext)
//            val rLayoutParamsTR =
//                TableRow.LayoutParams(
//                    TableRow.LayoutParams.MATCH_PARENT,
//                    TableRow.LayoutParams.WRAP_CONTENT
//                )
//            rLayoutParamsTR.setMargins(30, 20, 40, 0)
//            row.removeAllViews()
//
//            //row population
//            val tvIdTitle = TextView(applicationContext)
//            val tvNameTitle = TextView(applicationContext)
//            val tvSurnameTitle =
//                TextView(applicationContext)
//            val tvDateTItle = TextView(applicationContext)
//            tvIdTitle.text = "ID"
//            tvIdTitle.textSize = 20f
//            tvIdTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
//            tvNameTitle.text = "Name"
//            tvNameTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
//            tvNameTitle.textSize = 20f
//            tvSurnameTitle.text = "Surname"
//            tvSurnameTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
//            tvSurnameTitle.textSize = 20f
//            tvDateTItle.text = "Birth"
//            tvDateTItle.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
//            tvDateTItle.textSize = 20f
//            row.addView(tvIdTitle, rLayoutParamsTR)
//            row.addView(tvNameTitle, rLayoutParamsTR)
//            row.addView(tvSurnameTitle, rLayoutParamsTR)
//            row.addView(tvDateTItle, rLayoutParamsTR)
//            table.addView(row, layoutParamsT)
//            for (stud in students) {
//                val rowEl = TableRow(applicationContext)
//                //table customization
//                val layoutParams =
//                    TableLayout.LayoutParams(
//                        TableLayout.LayoutParams.MATCH_PARENT,
//                        TableLayout.LayoutParams.WRAP_CONTENT
//                    )
//                layoutParams.setMargins(30, 20, 40, 20)
//                val rLayoutParams =
//                    TableRow.LayoutParams(
//                        TableRow.LayoutParams.MATCH_PARENT,
//                        TableRow.LayoutParams.WRAP_CONTENT
//                    )
//                rLayoutParams.setMargins(30, 20, 40, 0)
//
//                //table population
//                val id: Int = stud.getId()
//                val name: String = stud.getName()
//                val surname: String = stud.getSurname()
//                val millis: Long = stud.getBorn()
//                val tvId = TextView(applicationContext)
//                val tvName = TextView(applicationContext)
//                val tvSurname =
//                    TextView(applicationContext)
//                val tvDate = TextView(applicationContext)
//                val formatter = SimpleDateFormat("dd/MM/yyyy")
//                val dateString = formatter.format(Date(millis))
//                tvId.text = id.toString()
//                tvId.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
//                tvName.text = name
//                tvName.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
//                tvSurname.text = surname
//                tvSurname.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
//                tvDate.text = dateString
//                tvDate.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
//                rowEl.addView(tvId, rLayoutParams)
//                rowEl.addView(tvName, rLayoutParams)
//                rowEl.addView(tvSurname, rLayoutParams)
//                rowEl.addView(tvDate, rLayoutParams)
//                table.addView(rowEl, layoutParams)
//            }
//        }
//        showExam.setOnClickListener { v: View? ->
//            val exams: ArrayList<Exam> = db.getAllExam()
//            val table =
//                findViewById<TableLayout>(R.id.table)
//            //table customization
//            val layoutParamsT =
//                TableLayout.LayoutParams(
//                    TableLayout.LayoutParams.MATCH_PARENT,
//                    TableLayout.LayoutParams.WRAP_CONTENT
//                )
//            layoutParamsT.setMargins(30, 20, 40, 0)
//            val rLayoutParamsTR =
//                TableRow.LayoutParams(
//                    TableRow.LayoutParams.MATCH_PARENT,
//                    TableRow.LayoutParams.WRAP_CONTENT
//                )
//            rLayoutParamsTR.setMargins(30, 20, 40, 0)
//            table.removeAllViews()
//            val row = TableRow(applicationContext)
//            row.removeAllViews()
//
//            //table population
//            val tvIdTitle =
//                TextView(applicationContext)
//            tvIdTitle.textSize = 20f
//            tvIdTitle.setTextColor(
//                ContextCompat.getColor(
//                    applicationContext,
//                    R.color.black
//                )
//            )
//            val tvNameTitle =
//                TextView(applicationContext)
//            tvNameTitle.textSize = 20f
//            tvNameTitle.setTextColor(
//                ContextCompat.getColor(
//                    applicationContext,
//                    R.color.black
//                )
//            )
//            val tvSurnameTitle =
//                TextView(applicationContext)
//            tvSurnameTitle.textSize = 20f
//            tvSurnameTitle.setTextColor(
//                ContextCompat.getColor(
//                    applicationContext,
//                    R.color.black
//                )
//            )
//            tvIdTitle.text = "Exam ID"
//            tvNameTitle.text = "Student ID"
//            tvSurnameTitle.text = "Mark"
//            row.addView(tvIdTitle, rLayoutParamsTR)
//            row.addView(tvNameTitle, rLayoutParamsTR)
//            row.addView(tvSurnameTitle, rLayoutParamsTR)
//            table.addView(row, layoutParamsT)
//            for (e in exams) {
//                val rowEl =
//                    TableRow(applicationContext)
//
//                //table customization
//                val layoutParams =
//                    TableLayout.LayoutParams(
//                        TableLayout.LayoutParams.MATCH_PARENT,
//                        TableLayout.LayoutParams.WRAP_CONTENT
//                    )
//                layoutParams.setMargins(30, 20, 40, 20)
//                val rLayoutParams =
//                    TableRow.LayoutParams(
//                        TableRow.LayoutParams.MATCH_PARENT,
//                        TableRow.LayoutParams.WRAP_CONTENT
//                    )
//                rLayoutParams.setMargins(30, 20, 40, 0)
//
//                //table population
//                val id: Int = e.getId()
//                val stud: Int = e.getStudent()
//                val eval: Int = e.getEvaluation()
//                val tvId =
//                    TextView(applicationContext)
//                val tvName =
//                    TextView(applicationContext)
//                val tvSurname =
//                    TextView(applicationContext)
//                tvId.text = id.toString()
//                tvId.setTextColor(
//                    ContextCompat.getColor(
//                        applicationContext,
//                        R.color.black
//                    )
//                )
//                tvName.text = stud.toString()
//                tvName.setTextColor(
//                    ContextCompat.getColor(
//                        applicationContext,
//                        R.color.black
//                    )
//                )
//                tvSurname.text = eval.toString()
//                tvSurname.setTextColor(
//                    ContextCompat.getColor(
//                        applicationContext,
//                        R.color.black
//                    )
//                )
//                rowEl.addView(tvId, rLayoutParams)
//                rowEl.addView(tvName, rLayoutParams)
//                rowEl.addView(tvSurname, rLayoutParams)
//                table.addView(rowEl, layoutParams)
//            }
//        }
//        clear.setOnClickListener { v: View? ->
//            val table =
//                findViewById<TableLayout>(R.id.table)
//            table.removeAllViews()
//        }
//    }

    private fun setTodayDate() {
        val selectedDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        binding!!.todayDateTv.text = ""+dateFormat.format(selectedDate.time)
    }

    override fun onResume() {
        super.onResume()
        populateRecyclerView()
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

        adapter = MainListAdapter(data)
        binding!!.recyclerView.adapter = adapter

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