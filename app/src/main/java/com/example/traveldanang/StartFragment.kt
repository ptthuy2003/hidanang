package com.example.traveldanang

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.sign

class StartFragment : Fragment() {
    val auth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (auth.currentUser != null) {
            val i = Intent(requireContext(), MainActivity::class.java)
            startActivity(i)
            return null
        }
        return inflater.inflate(R.layout.fragment_startscreen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        if (auth.currentUser == null) {
            val loginBtn = view.findViewById<AppCompatButton>(R.id.loginBtn)
            loginBtn.setOnClickListener {
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                fragmentManager.beginTransaction()
                    .add(R.id.frameAndroidLarge, LoginFragment()).addToBackStack(null)
                    .commit()
            }

            val signUpBtn = view.findViewById<AppCompatButton>(R.id.signupBtn)
            signUpBtn.setOnClickListener {
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                fragmentManager.beginTransaction()
                    .add(R.id.frameAndroidLarge, SignUpFragment()).addToBackStack(null)
                    .commit()
            }

            val startBtn: AppCompatButton = view.findViewById(R.id.btnStart)
            startBtn.setOnClickListener{
                val i = Intent(requireContext(), MainActivity::class.java)
                startActivity(i)
            }
        }
    }
}