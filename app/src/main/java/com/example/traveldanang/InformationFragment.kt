package com.example.traveldanang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import java.util.UUID

class InformationFragment : Fragment() {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    var image: Uri? = null
    lateinit var activityChoosePhoto: ActivityResultLauncher<Intent>
    lateinit var avatar: ImageView
    lateinit var usernameTxt: EditText
    lateinit var passwordTxt: EditText
    lateinit var phoneTxt: EditText
    lateinit var emailTxt: EditText
    lateinit var id: TextView
    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userJson = arguments?.getString("user")
        user = Gson().fromJson(userJson, User::class.java)
        usernameTxt = view.findViewById(R.id.usernameInputInfor)
        passwordTxt = view.findViewById(R.id.passwordInputInfor)
        phoneTxt = view.findViewById(R.id.phoneInputInfor)
        emailTxt = view.findViewById(R.id.emailInputInfor)
        id = view.findViewById(R.id.idInfor)
        avatar = view.findViewById<ImageView>(R.id.imageViewCurrentInfor)
        val uploadBtn = view.findViewById<Button>(R.id.uploadInforBtn)
        if (user != null) {
            usernameTxt.setText(user.getUsername());
            passwordTxt.setText(user.getPassword());
            phoneTxt.setText(user.getPhone());
            emailTxt.setText(user.getEmail());
            id.text = user.getId();
            Glide.with(avatar.context).load(user.getAvatar()).placeholder(R.drawable.placeholder).circleCrop().into(avatar)
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

        val updateBtn: Button = view.findViewById(R.id.updateBtnSubmitInfor)
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
                db.collection("user").document(id.text.toString()).update(userUpdate as Map<String, Any>).addOnSuccessListener{
                    Toast.makeText(requireContext(),"Updated Successfully!", Toast.LENGTH_LONG).show()
                    auth.createUserWithEmailAndPassword(email, password)
                }.addOnFailureListener{
                    Toast.makeText(requireContext(),"Error! Please check your data and try again!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun uploadImageAndUpdateUser() {
        var username = usernameTxt.text.toString()
        var password = passwordTxt.text.toString()
        var phone = phoneTxt.text.toString()
        var email = emailTxt.text.toString()

        var path = "users/" + UUID.randomUUID().toString()
        var storage = FirebaseStorage.getInstance()
        if (user.getAvatar() != "") {
            var currentAvatar = storage.getReferenceFromUrl(user.getAvatar())
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
                        db.collection("user").document(id.text.toString()).update(user as Map<String, Any>).addOnSuccessListener{
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
                    db.collection("user").document(id.text.toString()).update(user as Map<String, Any>).addOnSuccessListener {
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