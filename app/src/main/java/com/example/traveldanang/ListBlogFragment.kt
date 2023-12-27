package com.example.traveldanang

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListBlogFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val txtSearch = view.findViewById<EditText>(R.id.txtSearch)
        txtSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                search(view)
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }
        val searchBtn = view.findViewById<ImageButton>(R.id.btnSearch)
        searchBtn.setOnClickListener {
            search(view)
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        db.collection("blogs").limit(10).get().addOnSuccessListener {
            val tourDatas = ArrayList<Blog>()
            for (tour in it) {
                val data = Blog(
                    tour.id,
                    tour.get("content"),
                    tour.get("images") as ArrayList<String>,
                    tour.get("title"),
                    tour.get("summary"),
                )
                tourDatas.add(data)
            }
            val rv = view.findViewById<RecyclerView>(R.id.recycleViewListBlog)
            val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            rv.layoutManager = linearLayoutManager
            rv.adapter = BlogAdapter(requireContext(), tourDatas)
        }
    }

    private fun search(view: View) {
        val txtSearch = view.findViewById<EditText>(R.id.txtSearch)
        if (txtSearch.text.toString() != "" ) {
            db.collection("blogs").whereGreaterThanOrEqualTo("title", txtSearch.text.toString()).whereLessThanOrEqualTo("title", txtSearch.text.toString() + "\uf8ff")
                .get().addOnSuccessListener {
                    val tourDatas = ArrayList<Blog>()
                    for (tour in it) {
                        val data = Blog(
                            tour.id,
                            tour.get("content"),
                            tour.get("images") as ArrayList<String>,
                            tour.get("title"),
                            tour.get("summary"),
                        )
                        tourDatas.add(data)
                    }
                    val rv = view.findViewById<RecyclerView>(R.id.recycleViewListBlog)
                    val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    rv.layoutManager = linearLayoutManager
                    rv.adapter = BlogAdapter(requireContext(), tourDatas)
                }
        }
    }
}