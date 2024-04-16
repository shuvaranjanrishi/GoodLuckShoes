package com.example.goodluckshoes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainListAdapter(private val mList: List<ItemModel>) :
    RecyclerView.Adapter<MainListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_main, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        holder.dateTv.text = ItemsViewModel.date
        holder.text1.text = ItemsViewModel.text1
        holder.text2.text = ItemsViewModel.text2
        holder.text3.text = ItemsViewModel.text3
        holder.text4.text = ItemsViewModel.text4
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val dateTv: TextView = itemView.findViewById(R.id.dateTv)
        val text1: TextView = itemView.findViewById(R.id.text1)
        val text2: TextView = itemView.findViewById(R.id.text2)
        val text3: TextView = itemView.findViewById(R.id.text3)
        val text4: TextView = itemView.findViewById(R.id.text4)
    }
}