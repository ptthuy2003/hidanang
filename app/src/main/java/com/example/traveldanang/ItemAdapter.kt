package com.example.traveldanang

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class ItemAdapter(
    private val context: Context,
    private val userArrayList: ArrayList<User>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view)
    {
        val db = FirebaseFirestore.getInstance()
        val tvUsername = view.findViewById<TextView>(R.id.txtUsername)
        val tvPhone = view.findViewById<TextView>(R.id.txtPhone)
        val tvPassword = view.findViewById<TextView>(R.id.txtPassword)
        val tvEmail = view.findViewById<TextView>(R.id.txtEmail)
        val tvId = view.findViewById<TextView>(R.id.txtId)
        val avatar = view.findViewById<ImageView>(R.id.imageView)
        val layout = view.findViewById<LinearLayout>(R.id.item_layout)
        init {
            layout.setOnClickListener {
                db.collection("user").document(tvId.text.toString()).get().addOnSuccessListener { userDB ->
                    val user = User(
                        id = userDB.id,
                        username = userDB.getString("username")!!,
                        password = userDB.getString("password")!!,
                        email = userDB.getString("email")!!,
                        phone = userDB.getString("phone")!!,
                        avatar = userDB.getString("avatar")!!,
                        role = userDB.getString("role")!!
                    )
                    val userJson = Gson().toJson(user)
                    val bundle = Bundle()
                    bundle.putString("user", userJson)
                    val updateFragment = ProfileFragment()
                    updateFragment.arguments = bundle

//                    val fragmentManager =
//                        (layout.context as AppCompatActivity).supportFragmentManager
//                    fragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, updateFragment).addToBackStack("Main")
//                        .commit()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val modal: User = userArrayList[position]
        holder.tvUsername.text = modal.getUsername()
        holder.tvPassword.text = modal.getPassword()
        holder.tvPhone.text = modal.getPhone()
        holder.tvEmail.text = modal.getEmail()
        holder.tvId.text = modal.getId()
        Glide.with(holder.avatar.context).load(modal.getAvatar()).placeholder(R.drawable.placeholder).circleCrop().into(holder.avatar)
    }

    override fun getItemCount(): Int {
        return userArrayList.size
    }


}