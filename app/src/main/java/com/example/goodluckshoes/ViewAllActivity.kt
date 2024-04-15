package com.example.goodluckshoes

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.goodluckshoes.databinding.ActivityMainBinding
import com.example.goodluckshoes.databinding.ActivityViewAllBinding
import java.util.Calendar

class ViewAllActivity : AppCompatActivity() {

    var binding: ActivityViewAllBinding? = null

    var total1 = "0"
    var total2 = "0"
    var total3 = "0"
    var total4 = "0"
    var date: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)

        if (intent != null) {
            if (!intent.extras!!.isEmpty) {
                date = intent.getStringExtra("DATE").toString()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        populateRecyclerView()
    }

    @SuppressLint("Range", "SetTextI18n")
    private fun populateRecyclerView() {
        val db = DbHelper(this, null)
        val cursor = db.getExpenseByDate(date)!!
        Log.d("TAG", "cursor ${cursor.count}")

        val data = ArrayList<ItemModel>()

        while (cursor.moveToNext()) {
            val date = cursor.getString(cursor.getColumnIndex(DbHelper.DATE_COl))
            val amount1 = cursor.getString(cursor.getColumnIndex(DbHelper.AMOUNT1_COl))
            val amount2 = cursor.getString(cursor.getColumnIndex(DbHelper.AMOUNT2_COl))
            val amount3 = cursor.getString(cursor.getColumnIndex(DbHelper.AMOUNT3_COl))
            val amount4 = cursor.getString(cursor.getColumnIndex(DbHelper.AMOUNT4_COl))
            val createdAt = cursor.getString(cursor.getColumnIndex(DbHelper.CREATED_DATE_COL))

            data.add(ItemModel(date, amount1, amount2, amount3, amount4, createdAt))
            Log.d("TAG", "List: $data")
        }
        val adapter = ListViewAdapter(data)
        binding!!.recyclerView.adapter = adapter

        for (item in data) {
            total1 = (total1.toDouble() + item.text1.toDouble()).toString()
            total2 = (total2.toDouble() + item.text2.toDouble()).toString()
            total3 = (total3.toDouble() + item.text3.toDouble()).toString()
            total4 = (total4.toDouble() + item.text4.toDouble()).toString()
        }

        binding!!.createDateTv.text = "($date)"
        binding!!.totalTv.text = "মোট\n(" + data.size.toString() + ")"
        binding!!.total1.text = total1
        binding!!.total2.text = total2
        binding!!.total3.text = total3
        binding!!.total4.text = total4

        cursor.close()
    }
}