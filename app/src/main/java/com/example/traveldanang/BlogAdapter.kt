package com.example.traveldanang

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BlogAdapter(
    private val context: Context,
    private val blogArrayList: ArrayList<Blog>
) : RecyclerView.Adapter<BlogAdapter.ItemViewHolder>() {
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view)
    {
        var id = ""
        val tvTitle = view.findViewById<TextView>(R.id.textTitleBlog)
        val tvSummary = view.findViewById<TextView>(R.id.textSummaryBlog)
        val image = view.findViewById<ImageView>(R.id.imageBlog)
        val layout = view.findViewById<CardView>(R.id.cardBlog)
        init {
            layout.setOnClickListener {
                val intent = Intent(view.context, DetailsBlogActivity::class.java)
                intent.putExtra("idBlog", id)
                view.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.blog_horizontal, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val modal: Blog = blogArrayList[position]
        holder.tvTitle.text = modal.getTitle().toString()
        holder.tvSummary.text = modal.getSummary().toString()
        holder.id = modal.getId().toString()
        Glide.with(holder.image.context).load(modal.getImages()[0]).centerCrop().into(holder.image)
    }

    override fun getItemCount(): Int {
        return blogArrayList.size
    }


}