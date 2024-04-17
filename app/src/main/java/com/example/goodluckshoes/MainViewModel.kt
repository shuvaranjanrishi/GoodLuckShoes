package com.example.goodluckshoes

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val data = MutableLiveData<MutableList<ItemModel>>()

    val items = mutableListOf<ItemModel>()

    fun getData(db: DbHelper) {

        val cursor = db.getExpense()!!

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(0)
                val date = cursor.getString(1)
                val amount1 = cursor.getString(2)
                val amount2 = cursor.getString(3)
                val amount3 = cursor.getString(4)
                val amount4 = cursor.getString(5)
                val createdAt = cursor.getString(6)

                items.add(ItemModel(id, date, amount1, amount2, amount3, amount4))

            } while (cursor.moveToNext());
        }
        data.postValue(items)
    }

    fun getDataByDate(db: DbHelper, date: String) {

        val cursor = db.getExpenseByDate(date)!!

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(0)
                val date = cursor.getString(1)
                val amount1 = cursor.getString(2)
                val amount2 = cursor.getString(3)
                val amount3 = cursor.getString(4)
                val amount4 = cursor.getString(5)
                val createdAt = cursor.getString(6)

                items.add(ItemModel(id, date, amount1, amount2, amount3, amount4))

            } while (cursor.moveToNext());
        }
        data.postValue(items)
    }
}