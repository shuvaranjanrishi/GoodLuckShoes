package com.example.goodluckshoes

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class DayListAdapter(private val context: Context, private val mList: List<DayItemModel>) :
    RecyclerView.Adapter<DayListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_day_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        holder.dateTv.text = ItemsViewModel.date
        holder.text1.text = ItemsViewModel.text1

        holder.itemView.setOnClickListener {
            Log.d("parent",""+ItemsViewModel.date);

            val intent = Intent(context,ViewAllActivity::class.java)
            intent.putExtra("DATE",""+ItemsViewModel.date)
            context.startActivity(intent)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val dateTv: TextView = itemView.findViewById(R.id.dateTv)
        val text1: TextView = itemView.findViewById(R.id.text1)
    }
}