package com.example.traveldanang

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(
    private val context: Context,
    private val imageList: ArrayList<String>,
    private val listener: OnImageClickListener
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.imageSmall)
        val layout = view.findViewById<CardView>(R.id.imageLayout)
        init {
            layout.setOnClickListener {
                val drawable = image.drawable
                listener.onImageClick(drawable)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapter.ImageViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ImageAdapter.ImageViewHolder, position: Int) {
        val modal: String = imageList[position]
        Glide.with(holder.image.context).load(modal).into(holder.image)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}