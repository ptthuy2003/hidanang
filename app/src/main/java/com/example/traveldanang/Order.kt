package com.example.traveldanang

import com.google.firebase.Timestamp
import java.sql.Time
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Order(id: String, tour: Tour, adult: Any?, child: Any?, note: Any?, startTime: Timestamp, total: Number?) {
    private val id: String = id
    private val tour: Tour = tour
    private val adult: Any? = adult
    private val child: Any? = child
    private val note: Any? = note
    private val startTime: Timestamp = startTime
    private val total: Number? = total

    fun formatCurrency(amount: Number?): String {
        val localeVN = Locale("vi", "VN")
        val currencyFormatter = NumberFormat.getCurrencyInstance(localeVN)
        return currencyFormatter.format(amount)
    }
    fun convertTimestampToDate(timestamp: Timestamp): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = timestamp.toDate()
        return dateFormat.format(date)
    }
    fun getId() : String {
        return id
    }

    fun getTour() : Tour {
        return tour
    }

    fun getStartTime() : String {
        return convertTimestampToDate(startTime)
    }

    fun getTotal() : String? {
        return formatCurrency(total)
    }
}