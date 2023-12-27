package com.example.traveldanang

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class DetailsBlogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blogdetail)
        val id = intent.getStringExtra("idBlog")
        val db = FirebaseFirestore.getInstance()
        db.collection("blogs").document(id.toString()).get().addOnSuccessListener {
            val imageBig = findViewById<ImageView>(R.id.imageBigBlog)

            val rv = findViewById<RecyclerView>(R.id.recycleViewBlogDetails)
            val linearLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            val listImage = it.get("images") as ArrayList<String>
            Glide.with(imageBig.context).load(listImage.get(0)).into(imageBig)
            rv.layoutManager = linearLayoutManager
            rv.adapter = ImageAdapter(this, listImage, object : OnImageClickListener {
                override fun onImageClick(drawable: Drawable) {
                    imageBig.setImageDrawable(drawable)
                }
            })

            val textTitle = findViewById<TextView>(R.id.titleDetailsBlog)
            textTitle.text = it.get("title").toString()

            val webView = findViewById<WebView>(R.id.contentBlog)
            val webViewSettings = webView.settings
            webViewSettings.javaScriptEnabled = true
            webView.loadData(it.get("content").toString(), "text/html; charset=UTF-8", null)

            val backBtn = findViewById<AppCompatImageButton>(R.id.backBtnBlogDetails)
            backBtn.setOnClickListener {
                finish()
            }
        }
    }


}