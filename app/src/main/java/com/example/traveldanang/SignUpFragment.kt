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
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import java.util.UUID


class SignUpFragment : Fragment() {
    lateinit var db: FirebaseFirestore
    private val auth = FirebaseAuth.getInstance()
    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var phoneInput: EditText
    lateinit var emailInput: EditText
    private var image: Uri? = null
    lateinit var imageView: ImageView
    lateinit var btnUpload: Button
    lateinit var btnAdd: Button
    lateinit var activityChoosePhoto: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        activityChoosePhoto = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
                if (it != null) {
                    onActivityResult(it)
                }
        }
        usernameInput = view.findViewById(R.id.usernameSignup)
        passwordInput = view.findViewById(R.id.passwordSignup)
        phoneInput = view.findViewById(R.id.phoneSignup)
        emailInput = view.findViewById(R.id.emailSignup)
        btnAdd = view.findViewById(R.id.btnSignup)
        imageView = view.findViewById(R.id.imageViewAvatar)
        btnUpload = view.findViewById(R.id.uploadBtn)

        btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            activityChoosePhoto.launch(intent)
        }
        btnAdd.setOnClickListener {
            uploadImageAndSaveUser()
        }
    }

    private fun uploadImageAndSaveUser() {
        var username = usernameInput.text.toString()
        var password = passwordInput.text.toString()
        var phone = phoneInput.text.toString()
        var email = emailInput.text.toString()

        var path = "users/" + UUID.randomUUID().toString()
        if (image != null) {
            var storage = FirebaseStorage.getInstance()
            var reference = storage.reference.child(path)
            reference.putFile(image!!).addOnSuccessListener {
                reference.downloadUrl.addOnSuccessListener { uri ->
                    val user = hashMapOf(
                        "username" to  username,
                        "password" to password,
                        "phone" to phone,
                        "email" to email,
                        "role" to "user",
                        "avatar" to uri.toString()
                    )
                    db.collection("user").add(user).addOnSuccessListener{
                        val bundle = Bundle()
                        bundle.putString("signup", "Đăng ký thành công!")
                        val loginFragment = LoginFragment()
                        loginFragment.arguments = bundle
                        parentFragmentManager.beginTransaction().replace(R.id.frameAndroidLarge, loginFragment).addToBackStack("Sign up").commit()
                        auth.createUserWithEmailAndPassword(email, password)
                    }.addOnFailureListener{
                        Toast.makeText(requireContext(),"Đã xảy ra lỗi! Vui lòng kiểm tra thông tin đăng ký", Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Đã có lỗi xảy ra khi tải ảnh lên", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onActivityResult(result: ActivityResult) {
        if (result.resultCode == -1 && result.data != null) {
            imageView.setImageURI(result.data!!.data)
            image = result.data!!.data
        }
    }
}