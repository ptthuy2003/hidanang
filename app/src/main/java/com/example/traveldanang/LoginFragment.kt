package com.example.traveldanang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.util.LogicUtils
import com.google.gson.Gson
import kotlinx.coroutines.awaitAll

class LoginFragment : Fragment() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginBtn: Button = view.findViewById(R.id.btnLogin)
        val usernameInput: EditText = view.findViewById(R.id.usernameLogin)
        val passwordInput: EditText = view.findViewById(R.id.passwordLogin)
        val message = arguments?.getString("signup")
        if (message != null) {
            val textAlert = view.findViewById<TextView>(R.id.textAlert)
            textAlert.text = message
        }
        loginBtn.setOnClickListener{
            signIn(usernameInput.text.toString(), passwordInput.text.toString())
        }

    }

    private fun signIn(username: String, password: String) {
        db.collection("user").whereEqualTo("username", username).whereEqualTo("password", password).get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result.size() == 1) {
                    it.result.documents[0].getString("email")
                        ?.let { it1 -> auth.signInWithEmailAndPassword(it1, password) }
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val authUser = auth.currentUser
                                if (authUser != null) {
                                    val intent = Intent(context, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                }
            }
            else {
                Toast.makeText(context,"Đã có lỗi xảy ra! Vui lòng kiểm tra thông tin đăng nhập", Toast.LENGTH_LONG).show()
            }
        }
    }

}