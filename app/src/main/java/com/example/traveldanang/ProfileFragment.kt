package com.example.traveldanang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import java.util.UUID
import kotlin.math.log

class ProfileFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    var image: Uri? = null
    lateinit var activityChoosePhoto: ActivityResultLauncher<Intent>
    lateinit var avatar: ImageView
    lateinit var usernameTxt: TextView
    lateinit var passwordTxt: EditText
    lateinit var phoneTxt: EditText
    lateinit var emailTxt: TextView
    var id = ""
    var avatarCurrent = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usernameTxt = view.findViewById(R.id.text_username)
        passwordTxt = view.findViewById(R.id.passwordInputUpdate)
        phoneTxt = view.findViewById(R.id.phoneInputUpdate)
        emailTxt = view.findViewById(R.id.text_email)
        avatar = view.findViewById<ImageView>(R.id.imageViewCurrent)
        val uploadBtn = view.findViewById<Button>(R.id.uploadNewBtn)
        if (auth.currentUser != null) {
            db.collection("user").whereEqualTo("email", auth.currentUser!!.email).get().addOnSuccessListener { users ->
                if (users.size() == 1) {
                    usernameTxt.setText(users.documents.get(0).get("username").toString());
                    passwordTxt.setText(users.documents.get(0).get("password").toString());
                    phoneTxt.setText(users.documents.get(0).get("phone").toString());
                    emailTxt.setText(users.documents.get(0).get("email").toString());
                    id = users.documents.get(0).id
                    avatarCurrent = users.documents.get(0).get("avatar").toString()
                    Glide.with(avatar.context).load(avatarCurrent).placeholder(R.drawable.placeholder).circleCrop().into(avatar)

                    if (users.documents.get(0).get("likedTour") != null) {
                        val tourIds = users.documents.get(0).get("likedTour") as ArrayList<String>
                        val tourDatas = ArrayList<Tour>()
                        for (i in tourIds) {
                            db.collection("tours").document(i).get().addOnSuccessListener {tour ->
                                val data = Tour(tour.id, tour.get("details"),
                                    tour.get("images") as ArrayList<String>, tour.get("infor"), tour.get("location"), tour.get("name"),
                                    tour.get("price") as Number?, tour.get("type"), tour.get("sold"))
                                tourDatas.add(data)
                                if (i == tourIds.last()) {
                                    val rv = view.findViewById<RecyclerView>(R.id.recycleViewTourLiked)
                                    val linearLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                                    rv.layoutManager = linearLayoutManager
                                    rv.adapter = TourAdapterVertical(requireContext(), tourDatas)
                                }
                            }

                        }
                    }

                    db.collection("orders").whereEqualTo("userId", users.documents.get(0).id).get().addOnSuccessListener { orders ->
                        val orderDatas = ArrayList<Order>()
                        for (order in orders) {
                            db.collection("tours").document(order.get("tourId").toString()).get().addOnSuccessListener { tour ->
                                val data = Tour(tour.id, tour.get("details"),
                                    tour.get("images") as ArrayList<String>, tour.get("infor"), tour.get("location"), tour.get("name"),
                                    tour.get("price") as Number?, tour.get("type"), tour.get("sold"))
                                val orderData = Order(order.id, data, order.get("adult"), order.get("child"), order.get("note"), order.get("startTime") as Timestamp, order.get("total").toString().toInt())
                                orderDatas.add(orderData)
                                if (order == orders.last()) {
                                    val rv = view.findViewById<RecyclerView>(R.id.recycleViewTourOrdered)
                                    val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                                    rv.layoutManager = linearLayoutManager
                                    rv.adapter = TourOrderedAdapter(requireContext(), orderDatas)
                                }
                            }
                        }
                    }

                }
            }
        }
        else {
            val intent = Intent(context, StartActivity::class.java)
            startActivity(intent)
        }
        activityChoosePhoto = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it != null) {
                onActivityResult(it)
            }
        }
        uploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            activityChoosePhoto.launch(intent)
        }

        val updateBtn: Button = view.findViewById(R.id.updateBtnSubmit)
        updateBtn.setOnClickListener {
            if (image != null) {
                uploadImageAndUpdateUser()
            }
            else {
                var username = usernameTxt.text.toString()
                var password = passwordTxt.text.toString()
                var phone = phoneTxt.text.toString()
                var email = emailTxt.text.toString()
                val userUpdate = hashMapOf(
                    "username" to  username,
                    "password" to password,
                    "phone" to phone,
                    "email" to email,
                )
                db.collection("user").document(id).update(userUpdate as Map<String, Any>).addOnSuccessListener{
                    Toast.makeText(requireContext(),"Updated Successfully!", Toast.LENGTH_LONG).show()
                    auth.createUserWithEmailAndPassword(email, password)
                }.addOnFailureListener{
                    Toast.makeText(requireContext(),"Error! Please check your data and try again!", Toast.LENGTH_LONG).show()
                }
            }
        }

        val logoutBtn = view.findViewById<ImageButton>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(context, StartActivity::class.java)
            startActivity(intent)
        }

    }
    private fun uploadImageAndUpdateUser() {
        var username = usernameTxt.text.toString()
        var password = passwordTxt.text.toString()
        var phone = phoneTxt.text.toString()
        var email = emailTxt.text.toString()

        var path = "users/" + UUID.randomUUID().toString()
        var storage = FirebaseStorage.getInstance()
        if (avatarCurrent != "") {
            var currentAvatar = storage.getReferenceFromUrl(avatarCurrent)
            currentAvatar.delete().addOnSuccessListener {
                var reference = storage.reference.child(path)
                reference.putFile(image!!).addOnSuccessListener {
                    reference.downloadUrl.addOnSuccessListener { uri ->
                        val user = hashMapOf(
                            "username" to  username,
                            "password" to password,
                            "phone" to phone,
                            "email" to email,
                            "avatar" to uri.toString()
                        )
                        db.collection("user").document(id).update(user as Map<String, Any>).addOnSuccessListener{
                            Toast.makeText(requireContext(),"Updated Successfully!", Toast.LENGTH_LONG).show()
                            auth.createUserWithEmailAndPassword(email, password)
                        }.addOnFailureListener{
                            Toast.makeText(requireContext(),"Error! Please check your data and try again!", Toast.LENGTH_LONG).show()
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),"Something error when upload avatar", Toast.LENGTH_LONG).show()
                }
            }
        }
        else {
            var reference = storage.reference.child(path)
            reference.putFile(image!!).addOnSuccessListener {
                reference.downloadUrl.addOnSuccessListener { uri ->
                    val user = hashMapOf(
                        "username" to  username,
                        "password" to password,
                        "phone" to phone,
                        "email" to email,
                        "avatar" to uri.toString()
                    )
                    db.collection("user").document(id).update(user as Map<String, Any>).addOnSuccessListener {
                        Toast.makeText(requireContext(),"Updated Successfully!", Toast.LENGTH_LONG).show()
                        auth.createUserWithEmailAndPassword(email, password)
                    }.addOnFailureListener{
                        Toast.makeText(requireContext(),"Error! Please check your data and try again!", Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Something error when upload avatar", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun onActivityResult(result: ActivityResult) {
        if (result.resultCode == -1 && result.data != null) {
            avatar.setImageURI(result.data!!.data)
            image = result.data!!.data!!
        }
    }
}