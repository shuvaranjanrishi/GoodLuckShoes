package com.example.goodluckshoes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ListViewAdapter(private val mList: List<ItemModel>) :
    RecyclerView.Adapter<ListViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        holder.dateTv.text = ItemsViewModel.date
        holder.text1.text = ItemsViewModel.text1
        holder.text2.text = ItemsViewModel.text2
        holder.text3.text = ItemsViewModel.text3
        holder.text4.text = ItemsViewModel.text4

        holder.editBtn.setOnClickListener {
            openUpdateDialog(ItemsViewModel, holder.itemView.context)
        }
    }

    private fun openUpdateDialog(item: ItemModel, context: Context?) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_update, null)
        val dateTv: TextView = view.findViewById(R.id.dateTv)
        val et2: EditText = view.findViewById(R.id.et2)
        val et3: EditText = view.findViewById(R.id.et3)
        val et4: EditText = view.findViewById(R.id.et4)
        val et5: EditText = view.findViewById(R.id.et5)
        val cancelBtn: Button = view.findViewById(R.id.cancelBtn)
        val saveBtn: Button = view.findViewById(R.id.saveBtn)

        //set data
        dateTv.text = "" + item.date
        et2.setText("" + item.text1)
        et3.setText("" + item.text2)
        et4.setText("" + item.text3)
        et5.setText("" + item.text4)

        val dialog: AlertDialog = AlertDialog.Builder(context!!)
            .setTitle(""+context.getString(R.string.change_data))
            .setView(view)
            .create()
        dialog.show()

        cancelBtn.setOnClickListener { dialog.dismiss() }

        saveBtn.setOnClickListener {

            val str1: String =
                if (et2.text.toString() == "") "0" else et2.text.toString()
            val str2: String =
                if (et3.text.toString() == "") "0" else et3.text.toString()
            val str3: String =
                if (et4.text.toString() == "") "0" else et4.text.toString()
            val str4: String =
                if (et5.text.toString() == "") "0" else et5.text.toString()

            if (str1 == "0" && str2 == "0" && str3 == "0" && str4 == "0") {
                Toast.makeText(context, ""+context.getString(R.string.enter_amount), Toast.LENGTH_SHORT)
                    .show()
            } else {

                val selectedDate = Calendar.getInstance()
                selectedDate.timeInMillis = System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
                val createdAt = dateFormat.format(selectedDate.time)

                val db = DbHelper(context,null)

                db.updateExpense(
                    ""+item.id,
                    "" + str1,
                    "" + str2,
                    "" + str3,
                    "" + str4,
                    "" + createdAt,
                )
                Toast.makeText(context, ""+context.getString(R.string.update_done), Toast.LENGTH_LONG).show()
            }
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
        val text2: TextView = itemView.findViewById(R.id.text2)
        val text3: TextView = itemView.findViewById(R.id.text3)
        val text4: TextView = itemView.findViewById(R.id.text4)
        val editBtn: ImageView = itemView.findViewById(R.id.editBtn)
    }
}