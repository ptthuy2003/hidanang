package com.example.traveldanang

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsOrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_order)
        val id = intent.getStringExtra("idOrder")
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            db.collection("user").whereEqualTo("email", auth.currentUser!!.email).get().addOnSuccessListener { users ->
                if (users.size() == 1) {
                    val textName = findViewById<TextView>(R.id.textNameDetailsOrder)
                    textName.text = users.documents.get(0).get("username").toString()
                    val textPhone = findViewById<TextView>(R.id.textPhoneDetailsOrder)
                    textPhone.text = users.documents.get(0).get("phone").toString()
                    val textEmail = findViewById<TextView>(R.id.textEmailDetailsOrder)
                    textEmail.text = users.documents.get(0).get("email").toString()
                    db.collection("orders").document(id.toString()).get().addOnSuccessListener {order ->
                        val textQuantityAdult = findViewById<TextView>(R.id.quantityAdultDetails)
                        textQuantityAdult.text = order.get("adult").toString()
                        val textQuantityChild = findViewById<TextView>(R.id.quantityChildDetails)
                        textQuantityChild.text = order.get("child").toString()
                        val totalDetails = findViewById<TextView>(R.id.totalDetails)
                        totalDetails.text = formatCurrency(order.get("total") as Number)
                        val date = order.get("startTime") as Timestamp
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
                        val dateFormated = dateFormat.format(date.toDate())
                        val textTime = findViewById<TextView>(R.id.startDateDetails)
                        textTime.text = dateFormated
                        val textNoteDetails = findViewById<TextView>(R.id.textNoteDetails)
                        textNoteDetails.text = order.get("note").toString()

                        db.collection("tours").document(order.get("tourId").toString()).get().addOnSuccessListener {tour ->
                            val listImage = tour.get("images") as ArrayList<String>
                            val imageBigOrderDetails = findViewById<ImageView>(R.id.imageBigOrderDetails)
                            Glide.with(imageBigOrderDetails.context).load(listImage.get(0)).into(imageBigOrderDetails)

                            val textNameTour = findViewById<TextView>(R.id.text_name_order_details)
                            textNameTour.text = tour.get("name").toString()

                            val textLocation = findViewById<TextView>(R.id.text_location_order_details)
                            textLocation.text = tour.get("location").toString()

                        }
                        val backBtn = findViewById<AppCompatImageButton>(R.id.backBtnOrderDetails)
                        backBtn.setOnClickListener {
                            finish()
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
}