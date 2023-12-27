package com.example.traveldanang

import android.content.Context
import android.content.Intent
import android.util.Log
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

class TourAdapter(
    private val context: Context,
    private val tourArrayList: ArrayList<Tour>
) : RecyclerView.Adapter<TourAdapter.ItemViewHolder>() {
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view)
    {
        var id = ""
        val tvName = view.findViewById<TextView>(R.id.text_name_horizon)
        val tvLocation = view.findViewById<TextView>(R.id.text_location_horizon)
        val tvPrice = view.findViewById<TextView>(R.id.text_price_horizon)
        val image = view.findViewById<ImageView>(R.id.imageView_horizon)
        val btn_like = view.findViewById<ToggleButton>(R.id.btn_like_list_tour)
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val layout = view.findViewById<CardView>(R.id.cardTour)
        init {
            layout.setOnClickListener {
                val intent = Intent(view.context, DetailsTourActivity::class.java)
                intent.putExtra("idTour", id)
                view.context.startActivity(intent)
            }

            if (auth.currentUser != null) {
                db.collection("user").whereEqualTo("email", auth.currentUser!!.email).get().addOnSuccessListener { users ->
                    if (users.size() == 1) {
                        var listLiked = ArrayList<String>()
                        if (users.documents.get(0).get("likedTour") != null) {
                            listLiked = users.documents.get(0).get("likedTour") as ArrayList<String>
                        }

                        btn_like.isChecked = listLiked.contains(id)

                        btn_like.setOnClickListener {
                            if (listLiked.contains(id)) {
                                listLiked.remove(id)
                                val userUpdate = hashMapOf(
                                    "likedTour" to listLiked,
                                )
                                db.collection("user").document(users.documents.get(0).id).update(userUpdate as Map<String, Any>)
                            }
                            else {
                                listLiked.add(id.toString())
                                val userUpdate = hashMapOf(
                                    "likedTour" to listLiked,
                                )
                                db.collection("user").document(users.documents.get(0).id).update(userUpdate as Map<String, Any>)
                            }
                        }
                    }
                }
            }
            else {
                btn_like.setOnClickListener {
                    val intent = Intent(view.context, StartActivity::class.java)
                    view.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.tour_horizontal, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val modal: Tour = tourArrayList[position]
        holder.tvName.text = modal.getName().toString()
        holder.tvLocation.text = modal.getLocation().toString()
        holder.tvPrice.text = modal.getPrice()
        holder.id = modal.getId().toString()
        Glide.with(holder.image.context).load(modal.getImages()[0]).centerCrop().into(holder.image)
    }

    override fun getItemCount(): Int {
        return tourArrayList.size
    }


}