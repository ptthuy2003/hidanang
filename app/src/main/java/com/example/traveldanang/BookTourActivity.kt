package com.example.traveldanang

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.webkit.WebView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookTourActivity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_tour)
        val id = intent.getStringExtra("idTour")
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            db.collection("user").whereEqualTo("email", auth.currentUser!!.email).get().addOnSuccessListener { users ->
                if (users.size() == 1) {
                    val textName = findViewById<TextView>(R.id.text_name_user_book_tour)
                    textName.text = users.documents.get(0).get("username").toString()
                    val textPhone = findViewById<TextView>(R.id.text_phone_user_book_tour)
                    textPhone.text = users.documents.get(0).get("phone").toString()
                    val textEmail = findViewById<TextView>(R.id.text_email_user_book_tour)
                    textEmail.text = users.documents.get(0).get("email").toString()
                    db.collection("tours").document(id.toString()).get().addOnSuccessListener {tour ->
                        val imageBig = findViewById<ImageView>(R.id.imageBigBookTour)

                        val listImage = tour.get("images") as ArrayList<String>
                        Glide.with(imageBig.context).load(listImage.get(0)).into(imageBig)

                        val textName = findViewById<TextView>(R.id.text_name_book_tour)
                        textName.text = tour.get("name").toString()

                        val textLocation = findViewById<TextView>(R.id.text_location_book_tour)
                        textLocation.text = tour.get("location").toString()

                        val formattedDate = dateFormat.format(System.currentTimeMillis() + 172800000)
                        val textDay = findViewById<TextView>(R.id.text_day)
                        textDay.text = formattedDate

                        val textPriceAdult = findViewById<TextView>(R.id.textPriceAdult)
                        textPriceAdult.text = formatCurrency(tour.get("price").toString().toInt())
                        val textPriceChild = findViewById<TextView>(R.id.textPriceChild)
                        textPriceChild.text = formatCurrency(0)
                        val textPriceTotal = findViewById<TextView>(R.id.text_total_price)
                        textPriceTotal.text = formatCurrency(tour.get("price").toString().toInt())
                        val removeBtnAdult = findViewById<ImageButton>(R.id.removeBtnAdult)
                        val textQuantityAdult = findViewById<TextView>(R.id.textQuantityAdult)
                        val textQuantityChild = findViewById<TextView>(R.id.textQuantityChild)
                        removeBtnAdult.setOnClickListener {

                            if (textQuantityAdult.text.toString().toInt() > 1) {
                                textQuantityAdult.text = (textQuantityAdult.text.toString().toInt() - 1).toString()
                                textPriceAdult.text = formatCurrency(textQuantityAdult.text.toString().toInt() * tour.get("price").toString().toInt())
                                textPriceTotal.text = formatCurrency(textQuantityAdult.text.toString().toInt() * tour.get("price").toString().toInt() + textQuantityChild.text.toString().toInt() * (tour.get("price").toString().toInt()/2))
                            }
                        }

                        val addBtnAdult = findViewById<ImageButton>(R.id.addBtnAdult)
                        addBtnAdult.setOnClickListener {
                            textQuantityAdult.text = (textQuantityAdult.text.toString().toInt() + 1).toString()
                            textPriceAdult.text = formatCurrency(textQuantityAdult.text.toString().toInt() * tour.get("price").toString().toInt())
                            textPriceTotal.text = formatCurrency(textQuantityAdult.text.toString().toInt() * tour.get("price").toString().toInt() + textQuantityChild.text.toString().toInt() * (tour.get("price").toString().toInt()/2))
                        }

                        val removeBtnChild = findViewById<ImageButton>(R.id.removeBtnChild)
                        removeBtnChild.setOnClickListener {
                            if (textQuantityChild.text.toString().toInt() > 0) {
                                textQuantityChild.text = (textQuantityChild.text.toString().toInt() - 1).toString()
                                textPriceChild.text = formatCurrency(textQuantityChild.text.toString().toInt() * (tour.get("price").toString().toInt()/2))
                                textPriceTotal.text = formatCurrency(textQuantityAdult.text.toString().toInt() * tour.get("price").toString().toInt() + textQuantityChild.text.toString().toInt() * (tour.get("price").toString().toInt()/2))
                            }
                        }

                        val addBtnChild = findViewById<ImageButton>(R.id.addBtnChild)
                        addBtnChild.setOnClickListener {
                            textQuantityChild.text = (textQuantityChild.text.toString().toInt() + 1).toString()
                            textPriceChild.text = formatCurrency(textQuantityChild.text.toString().toInt() * (tour.get("price").toString().toInt()/2))
                            textPriceTotal.text = formatCurrency(textQuantityAdult.text.toString().toInt() * tour.get("price").toString().toInt() + textQuantityChild.text.toString().toInt() * (tour.get("price").toString().toInt()/2))
                        }

                        val btnSelectDay = findViewById<ImageButton>(R.id.btnSelectDay)
                        btnSelectDay.setOnClickListener {
                            showDatePicker()
                        }
                        val backBtn = findViewById<AppCompatImageButton>(R.id.backBtnTourDetails)
                        backBtn.setOnClickListener {
                            finish()
                        }

                        val btnPay = findViewById<AppCompatButton>(R.id.btnPay)
                        btnPay.setOnClickListener {
                            val date = dateFormat.parse(textDay.text.toString())
                            val textNote = findViewById<EditText>(R.id.textNote)
                            val order = hashMapOf(
                                "userId" to users.documents.get(0).id,
                                "tourId" to tour.id,
                                "adult" to textQuantityAdult.text.toString().toInt(),
                                "child" to textQuantityChild.text.toString().toInt(),
                                "total" to textQuantityAdult.text.toString().toInt() * tour.get("price").toString().toInt() + textQuantityChild.text.toString().toInt() * (tour.get("price").toString().toInt()/2),
                                "startTime" to Timestamp(date),
                                "note" to textNote.text.toString()
                            )
                            db.collection("orders").add(order).addOnSuccessListener {
                                val intent = Intent(this, BookSuccessActivity::class.java)
                                intent.putExtra("idOrder", it.id)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
        }


    }

    fun formatCurrency(amount: Number?): String {
        val localeVN = Locale("vi", "VN")
        val currencyFormatter = NumberFormat.getCurrencyInstance(localeVN)
        return currencyFormatter.format(amount)
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(this, {DatePicker, year: Int, month: Int, day: Int ->
            val selectedDay = Calendar.getInstance()
            selectedDay.set(year, month, day)
            val formattedDate = dateFormat.format(selectedDay.time)
            val textDay = findViewById<TextView>(R.id.text_day)
            textDay.text = formattedDate
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() + 172800000
        datePickerDialog.show()
    }


}