package com.example.traveldanang

import android.net.Uri

class Blog (id: String, content: Any?, images: ArrayList<String>, title: Any?, summary: Any?) {
    private val id: String = id
    private val content: Any? = content
    private val images: ArrayList<String> = images
    private val title: Any? = title
    private val summary: Any? = summary
    fun getId() : Any?
    {
        return id
    }

    fun getContent(): Any?
    {
        return content
    }

    fun getImages(): ArrayList<String>
    {
        return images
    }

    fun getTitle(): Any?
    {
        return title
    }

    fun getSummary(): Any?
    {
        return summary
    }
}