package com.example.traveldanang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        var id = ""
        var listLiked = ArrayList<String>()
        if (auth.currentUser != null) {
            db.collection("user").whereEqualTo("email", auth.currentUser!!.email).get().addOnSuccessListener { users ->
                if (users.size() == 1) {
                    id = users.documents.get(0).id
                    db.collection("orders").whereEqualTo("userId",id).whereGreaterThan("startTime", Timestamp.now()).orderBy("startTime", Query.Direction.ASCENDING).limit(1).get()
                        .addOnSuccessListener { order ->
                            if (order.size() == 1) {
                                val tourNext = view.findViewById<LinearLayout>(R.id.tourNext)
                                tourNext.visibility = View.VISIBLE
                                Log.d("ha", order.size().toString())
                                db.collection("tours").document(order.documents.get(0).get("tourId").toString()).get().addOnSuccessListener {tour ->
                                    val imageTourNext = view.findViewById<ImageView>(R.id.imageTourNext)
                                    val listImages = tour.get("images") as ArrayList<String>
                                    Glide.with(this).load(listImages[0]).into(imageTourNext)
                                    val textTourNameNext = view.findViewById<TextView>(R.id.textTourNameNext)
                                    textTourNameNext.text = tour.get("name").toString()
                                    val date = order.documents.get(0).get("startTime") as Timestamp
                                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
                                    val dateFormated = dateFormat.format(date.toDate())
                                    val textTimeNext = view.findViewById<TextView>(R.id.textTimeNext)
                                    textTimeNext.text = "09:00 " + dateFormated
                                    val cal1 = Calendar.getInstance()
                                    val cal2 = Calendar.getInstance()

                                    cal1.time = date.toDate()
                                    cal2.time = Date()

                                    val diff = cal1.timeInMillis - cal2.timeInMillis
                                    val dateRemain = diff / (24*60*60*1000)

                                    val textRemainTime = view.findViewById<TextView>(R.id.textRemainTime)
                                    textRemainTime.text = "Còn " + dateRemain + " ngày"
                                }
                                val noTourNext = view.findViewById<TextView>(R.id.noTourNext)
                                noTourNext.visibility = View.GONE
                            }
                            else {
                                val tourNext = view.findViewById<LinearLayout>(R.id.tourNext)
                                tourNext.visibility = View.GONE
                                val noTourNext = view.findViewById<TextView>(R.id.noTourNext)
                                noTourNext.visibility = View.VISIBLE
                            }
                        }
                }
            }
        }
        else {
            val tourNext = view.findViewById<LinearLayout>(R.id.tourNext)
            tourNext.visibility = View.GONE
            val noTourNext = view.findViewById<TextView>(R.id.noTourNext)
            noTourNext.visibility = View.VISIBLE
        }
        db.collection("tours").orderBy("sold", Query.Direction.DESCENDING).limit(4).get().addOnSuccessListener {
            val tourDatas = ArrayList<Tour>()
            for (tour in it) {
                val data = Tour(tour.id, tour.get("details"),
                    tour.get("images") as ArrayList<String>, tour.get("infor"), tour.get("location"), tour.get("name"),
                    tour.get("price") as Number?, tour.get("type"), tour.get("sold"))
                tourDatas.add(data)
            }
            val tourIds = arrayOf(R.id.tour1, R.id.tour2, R.id.tour3, R.id.tour4)
            if (auth.currentUser != null) {
                db.collection("user").whereEqualTo("email", auth.currentUser!!.email).get().addOnSuccessListener { users ->
                    if (users.size() == 1) {
                        if (users.documents.get(0).get("likedTour") != null) {
                            listLiked = users.documents.get(0).get("likedTour") as ArrayList<String>
                        }
                        for (i in tourIds.indices) {
                            val tour = view.findViewById<CardView>(tourIds[i]) as ViewGroup
                            tour.setOnClickListener {
                                val intent = Intent(context, DetailsTourActivity::class.java)
                                intent.putExtra("idTour", tourDatas[i].getId().toString())
                                startActivity(intent)
                            }
                            val image = tour.findViewById<ImageView>(R.id.imageView)
                            Glide.with(this).load(tourDatas[i].getImages().get(0)).into(image)
                            val textName = tour.findViewById<TextView>(R.id.text_name)
                            textName.text = tourDatas[i].getName().toString()
                            val textLocation = tour.findViewById<TextView>(R.id.text_location)
                            textLocation.text = tourDatas[i].getLocation().toString()
                            val textPrice = tour.findViewById<TextView>(R.id.text_price)
                            textPrice.text = (tourDatas[i].getPrice())
                            val likeBtn = tour.findViewById<ToggleButton>(R.id.btn_like)
                            likeBtn.isChecked = listLiked.contains(tourDatas[i].getId())

                            likeBtn.setOnClickListener {
                                if (listLiked.contains(tourDatas[i].getId())) {
                                    listLiked.remove(tourDatas[i].getId())
                                    val userUpdate = hashMapOf(
                                        "likedTour" to listLiked,
                                    )
                                    db.collection("user").document(users.documents.get(0).id).update(userUpdate as Map<String, Any>)
                                }
                                else {
                                    listLiked.add(tourDatas[i].getId().toString())
                                    val userUpdate = hashMapOf(
                                        "likedTour" to listLiked,
                                    )
                                    db.collection("user").document(users.documents.get(0).id).update(userUpdate as Map<String, Any>)
                                }
                            }
                        }
                    }
                }
            }
            else {
                for (i in tourIds.indices) {
                    val tour = view.findViewById<CardView>(tourIds[i]) as ViewGroup
                    tour.setOnClickListener {
                        val intent = Intent(context, DetailsTourActivity::class.java)
                        intent.putExtra("idTour", tourDatas[i].getId().toString())
                        startActivity(intent)
                    }
                    val image = tour.findViewById<ImageView>(R.id.imageView)
                    Glide.with(this).load(tourDatas[i].getImages().get(0)).into(image)
                    val textName = tour.findViewById<TextView>(R.id.text_name)
                    textName.text = tourDatas[i].getName().toString()
                    val textLocation = tour.findViewById<TextView>(R.id.text_location)
                    textLocation.text = tourDatas[i].getLocation().toString()
                    val textPrice = tour.findViewById<TextView>(R.id.text_price)
                    textPrice.text = (tourDatas[i].getPrice())
                    val likeBtn = tour.findViewById<ToggleButton>(R.id.btn_like)
                    likeBtn.setOnClickListener {
                        val intent = Intent(context, StartActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

        db.collection("blogs").orderBy("createTime", Query.Direction.DESCENDING).limit(3).get().addOnSuccessListener {
            val blogDatas = ArrayList<Blog>()
            for (blog in it) {
                val data = Blog(blog.id, blog.get("content"),
                    blog.get("images") as ArrayList<String>, blog.get("title"), blog.get("summary"))
                blogDatas.add(data)
            }
            val blogIds = arrayOf(R.id.blog1, R.id.blog2, R.id.blog3)
            for (i in blogIds.indices) {
                val blog = view.findViewById<CardView>(blogIds[i]) as ViewGroup
                blog.setOnClickListener {
                    val intent = Intent(context, DetailsBlogActivity::class.java)
                    intent.putExtra("idBlog", blogDatas[i].getId().toString())
                    startActivity(intent)
                }
                val image = blog.findViewById<ImageView>(R.id.imageBlog)
                Glide.with(this).load(blogDatas[i].getImages().get(0)).into(image)
                val textTitle = blog.findViewById<TextView>(R.id.textTitleBlog)
                textTitle.text = blogDatas[i].getTitle().toString()
                val textSummary = blog.findViewById<TextView>(R.id.textSummaryBlog)
                textSummary.text = (blogDatas[i].getSummary().toString())

            }
        }
    }
}