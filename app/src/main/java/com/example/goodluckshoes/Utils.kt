package com.example.goodluckshoes

import android.R.id.input
import java.text.SimpleDateFormat
import java.util.Date


class Utils {
    companion object {
        fun getDayName(date: String): String {
            val inFormat = SimpleDateFormat("dd-MM-yy")
            val date: Date = inFormat.parse(date)
            val outFormat = SimpleDateFormat("EEEE")
            return outFormat.format(date)
        }
    }
}