package com.example.traveldanang

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.Date

class TourOrderedAdapter(
    private val context: Context,
    private val orderArrayList: ArrayList<Order>
) : RecyclerView.Adapter<TourOrderedAdapter.ItemViewHolder>() {
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view)
    {
        var id = ""
        val tvName = view.findViewById<TextView>(R.id.text_name_ordered)
        val tvLocation = view.findViewById<TextView>(R.id.text_location_ordered)
        val tvPrice = view.findViewById<TextView>(R.id.total_ordered)
        val image = view.findViewById<ImageView>(R.id.imageOrderedTour)
        val tvStartDay = view.findViewById<TextView>(R.id.start_day_ordered)
        val layout = view.findViewById<CardView>(R.id.cardOrdered)
        init {
            layout.setOnClickListener {
                val intent = Intent(view.context, DetailsOrderActivity::class.java)
                intent.putExtra("idOrder", id)
                view.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.ordered_tour, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val modal: Order = orderArrayList[position]
        holder.tvName.text = modal.getTour().getName().toString()
        holder.tvLocation.text = modal.getTour().getLocation().toString()
        holder.tvPrice.text = modal.getTotal()
        holder.id = modal.getId()
        holder.tvStartDay.text = modal.getStartTime()
        Glide.with(holder.image.context).load(modal.getTour().getImages()[0]).centerCrop().into(holder.image)
    }

    override fun getItemCount(): Int {
        return orderArrayList.size
    }


}