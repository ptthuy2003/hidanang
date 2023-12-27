package com.example.traveldanang

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class DetailsTourActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tourdetail)
        val id = intent.getStringExtra("idTour")
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        db.collection("tours").document(id.toString()).get().addOnSuccessListener {
            val imageBig = findViewById<ImageView>(R.id.imageBig)

            val rv = findViewById<RecyclerView>(R.id.recycleView)
            val linearLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            val listImage = it.get("images") as ArrayList<String>
            Glide.with(imageBig.context).load(listImage.get(0)).into(imageBig)
            rv.layoutManager = linearLayoutManager
            rv.adapter = ImageAdapter(this, listImage, object : OnImageClickListener {
                override fun onImageClick(drawable: Drawable) {
                    imageBig.setImageDrawable(drawable)
                }
            })




            val textNameDetails = findViewById<TextView>(R.id.text_name_details)
            textNameDetails.text = it.get("name").toString()

            val textLocationDetails = findViewById<TextView>(R.id.text_location_details)
            textLocationDetails.text = it.get("location").toString()

            val textPriceDetails = findViewById<TextView>(R.id.text_price_details)
            textPriceDetails.text = formatCurrency(it.get("price") as Number)

            val webViewInfo = findViewById<WebView>(R.id.webview_infor)
            val webInfoSettings = webViewInfo.settings
            webInfoSettings.javaScriptEnabled = true
            webViewInfo.loadData(it.get("infor").toString(), "text/html; charset=UTF-8", null)

            val webViewDetails = findViewById<WebView>(R.id.webview_details)
            val webDetailsSetting = webViewDetails.settings
            webDetailsSetting.javaScriptEnabled = true
            webViewDetails.loadData(it.get("details").toString(), "text/html; charset=UTF-8", null)

            val backBtn = findViewById<AppCompatImageButton>(R.id.backBtnTourDetails)
            backBtn.setOnClickListener {
                finish()
            }

            val btnBook = findViewById<AppCompatButton>(R.id.btnBook)
            btnBook.setOnClickListener {
                if (auth.currentUser == null) {
                    val intent = Intent(this, StartActivity::class.java)
                    startActivity(intent)
                }
                else {
                    val intent = Intent(this, BookTourActivity::class.java)
                    intent.putExtra("idTour", id)
                    startActivity(intent)
                }
            }

            val btn_like = findViewById<ToggleButton>(R.id.btn_like)
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
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun formatCurrency(amount: Number?): String {
        val localeVN = Locale("vi", "VN")
        val currencyFormatter = NumberFormat.getCurrencyInstance(localeVN)
        return currencyFormatter.format(amount)
    }

}