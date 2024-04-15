package com.example.goodluckshoes

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.goodluckshoes.databinding.ActivityDayListBinding
import com.example.goodluckshoes.databinding.ActivityViewAllBinding

class DayListActivity : AppCompatActivity() {

    var binding: ActivityDayListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDayListBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)
    }

    override fun onResume() {
        super.onResume()
        populateRecyclerView()
    }

    @SuppressLint("Range")
    private fun populateRecyclerView() {
        val db = DbHelper(this, null)
        val cursor = db.getDayList()!!

        val data = ArrayList<DayItemModel>()
        Log.d("TAG", "Cursor size: " + cursor.count)

        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(0)
                data.add(DayItemModel(date, "" + Utils.getDayName(date)))
            } while (cursor.moveToNext())
        }

        val adapter = DayListAdapter(this, data)
        binding!!.recyclerView.adapter = adapter

        cursor.close()
    }
}