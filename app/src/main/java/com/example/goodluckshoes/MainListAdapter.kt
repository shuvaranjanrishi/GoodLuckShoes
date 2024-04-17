package com.example.goodluckshoes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView


class MainListAdapter(private val context: Context, private val mList: List<ItemModel>) :
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

        holder.itemView.setOnLongClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    holder.itemLayout.setBackgroundColor(4)
                    val db = DbHelper(context, null)
                    val result = db.deleteExpense(ItemsViewModel.id)
                    if (result != -1) {
                        notifyItemRemoved(holder.adapterPosition)
                        notifyItemChanged(holder.adapterPosition)
                        notifyDataSetChanged()
                        Toast.makeText(
                            context,
                            ItemsViewModel.date + " is Deleted...",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                    notifyDataSetChanged()
                    holder.itemView.setBackgroundColor(999999)
                }
            val alert = builder.create()
            alert.show()

            false
        }
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
        val itemLayout: LinearLayout = itemView.findViewById(R.id.itemLayout)
    }
}