package com.example.traveldanang

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class BookSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_success)
        val id = intent.getStringExtra("idOrder")
        val homeBtn = findViewById<AppCompatButton>(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val detailsBtn = findViewById<AppCompatButton>(R.id.detailsOrderBtn)
        detailsBtn.setOnClickListener {
            val intent = Intent(this, DetailsOrderActivity::class.java)
            intent.putExtra("idOrder", id)
            startActivity(intent)
            finish()
        }
    }
}