package com.example.traveldanang

import android.net.Uri
import java.text.NumberFormat
import java.util.Locale

class Tour(id: String, details: Any?, images: ArrayList<String> , infor: Any?, location: Any?, name: Any?, price: Number?, type: Any?, sold: Any?) {
    private val id: String = id
    private val details: Any? = details
    private val images: ArrayList<String> = images
    private val infor: Any? = infor
    private val location: Any? = location
    private val name: Any? = name
    private val price: Number? = price
    private val type: Any? = type
    private val sold: Any? = sold


    fun formatCurrency(amount: Number?): String {
        val localeVN = Locale("vi", "VN")
        val currencyFormatter = NumberFormat.getCurrencyInstance(localeVN)
        return currencyFormatter.format(amount)
    }

    fun getId() : Any?
    {
        return id
    }

    fun getDetails(): Any?
    {
        return details
    }

    fun getImages(): ArrayList<String>
    {
        return images
    }

    fun getInfor(): Any?
    {
        return infor
    }

    fun getLocation(): Any?
    {
        return location
    }

    fun getName(): Any?
    {
        return name
    }

    fun getPrice(): String {
        return formatCurrency(price)
    }

    fun getType(): Any? {
        return type
    }

    fun getSold(): Any? {
        return sold
    }
}
